package org.esupportail.syncfsnx.domain;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.logging.impl.Log4JLogger;
import org.junit.Test;

public class BrowseFSTest {
	
	Log4JLogger logger = new Log4JLogger(this.getClass().getName());

	@Test
	public void test() throws URISyntaxException {
		URL url = getClass().getResource("/hierarchy");
		listFolder(new File(url.toURI()));
	}

	public void listFolder (File folder) {
		logger.info(folder.getAbsolutePath());
		if (folder.isDirectory()) {
			File[] files = folder.listFiles();
			if (files != null){
				for (File file : files) {
					listFolder(file);
				}
			}
		} 
	}

}
