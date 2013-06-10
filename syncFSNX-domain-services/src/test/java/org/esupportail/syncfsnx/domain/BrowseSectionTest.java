package org.esupportail.syncfsnx.domain;

import org.apache.commons.logging.impl.Log4JLogger;
import org.junit.Test;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

public class BrowseSectionTest {
	
	//cmis URL : http://localhost:8080/nuxeo/atom/cmis
	Log4JLogger logger = new Log4JLogger(this.getClass().getName());

	@Test
	public void test() throws Exception {
		HttpAutomationClient client = new HttpAutomationClient("http://localhost:8080/nuxeo/site/automation");
		Session session = client.getSession("test", "test");
		String path = "/default-domain/sections/test";
		ListSection(path, session);
	}
	
	private void ListSection(String path, Session session) throws Exception {
		Document root = (Document) session.newRequest("Document.Fetch").set("value", path).execute();
		Documents documents = (Documents) session.newRequest("Document.GetChildren").setInput(root).execute();
		for (Document document : documents) {
			String documentPath = document.getPath();
			logger.info(documentPath);
			if (document.getType().equals("Folder")) {
				ListSection(documentPath, session);
			}
		}
	}

}
