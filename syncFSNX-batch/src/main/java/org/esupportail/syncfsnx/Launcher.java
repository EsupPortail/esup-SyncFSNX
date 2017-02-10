/**
 * 
 */
package org.esupportail.syncfsnx;

import org.esupportail.syncfsnx.domain.DomainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author bourges
 *
 */
public class Launcher {

	/**
	 * For Logging.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(Launcher.class);
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LOGGER.info("Starting syncFSNX Launcher");
		//load spring config
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
		        new String[] {"META-INF/syncFSNX-domain-services-domain.xml"});
		DomainService domainService = (DomainService) appContext.getBean("domainService");
		//start synchronization
		domainService.synchronise();
		LOGGER.info("syncFSNX OK");
	}

}
