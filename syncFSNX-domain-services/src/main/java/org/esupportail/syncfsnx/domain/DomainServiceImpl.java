/**
 * ESUP-Portail Blank Application - Copyright (c) 2010 ESUP-Portail consortium.
 */
package org.esupportail.syncfsnx.domain;

import org.esupportail.commons.exceptions.ConfigException;
import org.esupportail.commons.utils.Assert;
import org.esupportail.syncfsnx.domain.beans.Configurator;
import org.esupportail.syncfsnx.domain.beans.SyncDocument;
import org.esupportail.syncfsnx.domain.beans.SyncDocumentType;
import org.nuxeo.client.api.NuxeoClient;
import org.nuxeo.client.api.objects.Document;
import org.nuxeo.client.api.objects.Documents;
import org.nuxeo.client.api.objects.acl.ACE;
import org.nuxeo.client.api.objects.upload.BatchUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Raymond Bourges
 */
public class DomainServiceImpl implements DomainService, InitializingBean {

    /**
     * For Serialize.
     */
    private static final long serialVersionUID = 5562208937407153456L;

    /**
     * For Logging.
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * configurator
     */
    private Configurator configurator;

    private AtomicReference<NuxeoClient> clientRef;

    @Override
    public void synchronise() {
        //get local documents
        logger.info("get local documents list");
        Map<String, SyncDocument> localDocuments = getLocalDocuments();
        //get remote documents
        logger.info("get remote documents list");
        Map<String, SyncDocument> remoteDocuments = getRemoteDocuments();
        //remove unnecessary remote documents (not presents locally)
        logger.info("remove unnecessary remote documents");
        removeUnnecessaryRemoteDocuments(localDocuments, remoteDocuments);
        //put local documents to remote server
        logger.info("put local documents to remote server");
        putLocalDocuments(localDocuments, remoteDocuments);
    }

    private void putLocalDocuments(
            Map<String, SyncDocument> localDocuments,
            Map<String, SyncDocument> remoteDocuments) {
        List<String> localKeys = new ArrayList<>(localDocuments.keySet());
        Collections.sort(localKeys);
        List<String> remoteKeys = new ArrayList<>(remoteDocuments.keySet());
        Collections.sort(remoteKeys);
        for (String localKey : localKeys) {
            SyncDocument local = localDocuments.get(localKey);
            //is local document is not present remotely ?
            if (remoteDocuments.get(localKey) == null) {
                createNxDocument(local);
            } else {
                SyncDocument remote = remoteDocuments.get(localKey);
                //is local document not equal remote document (for example modification date is different)
                if (!local.isFolder() && local.getModificationDate().after(remote.getModificationDate())) {
                    removeNxDocument(local);
                    createNxDocument(local);
                }
            }
        }
    }

    private void createNxDocument(SyncDocument local) {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating " + local.getRelativePath() + " on Nuxeo");
        }
        final NuxeoClient client = getNxClient();
        try {
            //find parent path
            final String localRelativePath = local.getRelativePath();
            final String remotePath = configurator.getRemotePath() + localRelativePath;
            final String remoteParentPath = remotePath.substring(0, remotePath.lastIndexOf("/"));
            final String finalPath = remotePath.substring(remotePath.lastIndexOf("/") + 1);

            final String aclFilename = configurator.getAclFileName();

            final int endIndex = localRelativePath.lastIndexOf("/");
            final Path aclFile = Paths.get(
                    configurator.getLocalPath(),
                    endIndex > 1 ? localRelativePath.substring(0, endIndex) : localRelativePath,
                    aclFilename);

            final Map<String, String[]> tuples = new HashMap<>();
            if (Files.exists(aclFile) && Files.isRegularFile(aclFile)) {
                final List<String> lines = Files.readAllLines(aclFile, StandardCharsets.UTF_8);
                for (String line: lines) {
                    if (!line.equals("")) {
                        final String[] fragments = line.split(";");
                        if (fragments.length < 3) throw new ConfigException("ACLs MUST consist of triplet");
                        final String user = fragments[1];
                        final String permission = fragments[2];
                        tuples.put(fragments[0], new String[]{user, permission});
                    }
                }
            }

            final Document root = client.repository().fetchDocumentByPath(remoteParentPath);
            if (local.isFolder()) {
                Document rep = new Document(finalPath, "Folder");
                rep.setName(finalPath);
                rep.set("dc:title", finalPath);

                final Document createdRep = client.repository().createDocumentByPath(root.getPath(), rep);

                final String[] acl = tuples.get(finalPath);
                if (acl != null) {
                    final ACE ace = new ACE();
                    ace.setUsername(acl[0]);
                    ace.setPermission(acl[1]);
                    ace.setBlockInheritance(true);
                    addPermission(createdRep, ace);
                }

            } else if (!finalPath.equalsIgnoreCase(aclFilename)) {

                final Document doc = new Document(finalPath, "File");
                doc.setName(finalPath);
                doc.set("dc:title", finalPath);

                final Document createdDoc = client.repository().createDocumentById(root.getId(), doc);

                final File file = Paths.get(configurator.getLocalPath(), local.getRelativePath()).toFile();
                //find mimeType
                final MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
                final String mimeType = mimeTypesMap.getContentType(file);

                BatchUpload batch = client.fetchUploadManager();
                batch = batch.upload(file.getName(), file.length(), mimeType, batch.getBatchId(), "1", file);

                createdDoc.set("file:content", batch.getBatchBlob());
                createdDoc.updateDocument();
            }
        } catch (Exception e) {
            error("Error creating " + local.getRelativePath() + " document", e);
        }
    }

    private void addPermission(Document document, ACE ace) {
        final Map<String, Object> params = new HashMap<>();
        params.put("user", ace.getUsername());
        params.put("permission", ace.getPermission());
        params.put("blockInheritance", ace.isBlockInheritance());
        getNxClient().automation("Document.AddPermission").input(document).parameters(params).execute();
    }

    private void removeUnnecessaryRemoteDocuments(
            Map<String, SyncDocument> localDocuments,
            Map<String, SyncDocument> remoteDocuments) {
        List<String> remoteKeys = new ArrayList<>(remoteDocuments.keySet());
        Collections.sort(remoteKeys);
        String path = "/////////////";
        for (String remoteKey : remoteKeys) {
            //is remote document is not present locally ?
            if (localDocuments.get(remoteKey) == null) {
                //test that current key is not a children of already removed file
                if (!remoteKey.startsWith(path)) {
                    //remove the remote document
                    SyncDocument document = remoteDocuments.get(remoteKey);
                    removeNxDocument(document);
                    path = document.getRelativePath();
                }
            }
        }
    }

    /**
     * @param document remove a document from nuxeo
     */
    private void removeNxDocument(SyncDocument document) {
        if (logger.isDebugEnabled()) {
            logger.debug("removing " + document.getRelativePath() + " from Nuxeo");
        }
        String path = configurator.getRemotePath() + document.getRelativePath();
        try {
            final Document root = getNxClient().repository().fetchDocumentByPath(path);
            getNxClient().repository().deleteDocument(root);
        } catch (Exception e) {
            error("Error removing " + path + " from nuxeo", e);
        }
    }

    private HashMap<String, SyncDocument> getRemoteDocuments() {
        final HashMap<String, SyncDocument> ret = new HashMap<>();
        final String path = configurator.getRemotePath();
        try {
            ret.putAll(listRemoteFolder(path));
        } catch (Throwable e) {
            error("Error reading nuxeo documents", e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("remote documents:");
            for (String key : ret.keySet()) {
                SyncDocument syncDocument = ret.get(key);
                logger.debug("-->" + syncDocument.getRelativePath());
            }
        }
        return ret;
    }

    /**
     * @param msg error message
     * @param e   exception
     *            log a error and throw it
     */
    private void error(String msg, Throwable e) {
        logger.error(msg);
        if (e != null) {
            throw new RuntimeException(msg, e);
        } else {
            throw new RuntimeException(msg);
        }
    }

    private Map<String, SyncDocument> listRemoteFolder(final String path) {
        final Map<String, SyncDocument> result = new HashMap<>();

        final Document root = getNxClient().repository().fetchDocumentByPath(path);
        final Documents docs = getNxClient().repository().fetchChildrenById(root.getId());
        for (Document document : docs.getDocuments()) {
            final SyncDocument current = new SyncDocument();
            final Date modificationDate = Date.from(Instant.parse(document.getLastModified()));
            current.setModificationDate(modificationDate);
            final String relativePath = document.getPath().replaceFirst(configurator.getRemotePath(), "");
            current.setRelativePath(relativePath);
            if (document.getType().equals("Folder")) {
                current.setDocumentType(SyncDocumentType.FOLDER);
                result.putAll(listRemoteFolder(document.getPath()));
            } else {
                current.setDocumentType(SyncDocumentType.FILE);
            }
            result.put(relativePath, current);
        }
        return result;
    }

    private Map<String, SyncDocument> getLocalDocuments() {
        final Map<String, SyncDocument> ret = new HashMap<>();
        final Path rootPath = Paths.get(configurator.getLocalPath());
        final File root = rootPath.toFile();
        if (!root.isDirectory()) {
            error(configurator.getLocalPath() + " must be a folder !", null);
        }
        ret.putAll(listLocalFolder(rootPath));
        if (logger.isDebugEnabled()) {
            logger.debug("local documents:");
            for (String key : ret.keySet()) {
                SyncDocument syncDocument = ret.get(key);
                logger.debug("-->" + syncDocument.getRelativePath());
            }
        }
        return ret;
    }

    private Map<String, SyncDocument> listLocalFolder(Path path) {
        final Map<String, SyncDocument> result = new HashMap<>();
        final File file = path.toFile();
        if (!file.isHidden()) {
            final SyncDocument current = new SyncDocument();
            final Date modificationDate = new Date(file.lastModified());
            current.setModificationDate(modificationDate);

            final String localPath = Paths.get(configurator.getLocalPath()).toFile().getAbsolutePath();
            final String relativePath = path.toFile().getAbsolutePath()
                    .replace(localPath, "")
                    .replaceAll("\\\\", "/");
            current.setRelativePath(relativePath);
            if (file.isDirectory()) {
                current.setDocumentType(SyncDocumentType.FOLDER);
                File[] files = file.listFiles();
                if (files != null) {
                    for (File child : files) {
                        result.putAll(listLocalFolder(child.toPath()));
                    }
                }
            } else {
                current.setDocumentType(SyncDocumentType.FILE);
            }
            if (!relativePath.equals("")) {
                result.put(relativePath, current);
            }
        }
        return result;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(configurator, "properties configurator of class " + this.getClass() + " can't be null!");
        Assert.hasText(configurator.getUser(), "Nuxeo user can't be empty!");
        Assert.hasText(configurator.getPassword(), "Nuxeo password can't be empty!");
        Assert.hasText(configurator.getNuxeoAutomationURL(), "Nuxeo URL can't be empty!");
        Assert.hasText(configurator.getLocalPath(), "Local path can't be empty!");
    }

    /**
     * @param configurator the configurator to set
     */
    public void setConfigurator(Configurator configurator) {
        this.configurator = configurator;
    }


    private NuxeoClient getNxClient() {
        if (clientRef == null || clientRef.get() == null) {
            this.clientRef = new AtomicReference<>(new NuxeoClient(
                    configurator.getNuxeoAutomationURL(), configurator.getUser(), configurator.getPassword()));
        }
        return clientRef.get();
    }
}
