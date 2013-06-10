package org.esupportail.syncfsnx.domain;

import org.junit.Test;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

public class RemoveSectionTest {
	
	//cmis URL : http://localhost:8080/nuxeo/atom/cmis

	@Test
	public void workInSection() throws Exception {
		HttpAutomationClient client = new HttpAutomationClient("http://localhost:8080/nuxeo/site/automation");
		Session session = client.getSession("test", "test");
		String worspacesPath = "/default-domain/sections/test";
		Document root = (Document) session.newRequest("Document.Fetch").set("value", worspacesPath + "/rep1").execute();
		session.newRequest("Document.Delete")
				.setInput(root).execute();
	}

}
