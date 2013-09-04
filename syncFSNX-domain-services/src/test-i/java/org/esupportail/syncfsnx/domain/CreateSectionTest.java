package org.esupportail.syncfsnx.domain;

import java.io.File;
import java.net.URL;

import org.junit.Test;
import org.nuxeo.ecm.automation.client.jaxrs.Constants;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.FileBlob;

public class CreateSectionTest {
	
	//cmis URL : http://localhost:8080/nuxeo/atom/cmis

	@Test
	public void workInSection() throws Exception {
		HttpAutomationClient client = new HttpAutomationClient("http://localhost:8080/nuxeo/site/automation");
		Session session = client.getSession("test", "test");
		String worspacesPath = "/default-domain/sections/test";
		Document root = (Document) session.newRequest("Document.Fetch").set("value", worspacesPath).execute();
		Document rep = (Document) session.newRequest("Document.Create")
				.setInput(root)
				.set("type", "Folder")
				.set("name", "rep1")
				.set("properties", "dc:title=rep1")
				.execute();
		Document doc = (Document) session.newRequest("Document.Create")
				.setInput(rep)
				.set("type", "File")
				.set("name", "file1")
				.set("properties", "dc:title=file1")
				.execute();
		URL url = getClass().getResource("/hierarchy/rep2/metro.pdf");
		File file = new File(url.toURI());
        FileBlob fb = new FileBlob(file);
        fb.setMimeType("application/pdf");
        session.newRequest("Blob.Attach")
        		.setHeader(Constants.HEADER_NX_VOIDOP, "true")
        		.setInput(fb)
                .set("document", doc).execute();
	}

}
