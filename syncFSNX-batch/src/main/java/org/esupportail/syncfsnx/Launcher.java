/**
 * 
 */
package org.esupportail.syncfsnx;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.syncfsnx.domain.DomainService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author bourges
 *
 */
public class Launcher {

	/**
	 * For Logging.
	 */
	private static final Logger LOGGER = new LoggerImpl(Launcher.class);
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
