/**
 * 
 */
package org.esupportail.syncfsnx.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author bourges
 *
 */
@ContextConfiguration(locations="/META-INF/syncFSNX-domain-services-domain.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class DomainServiceTest {
	
	DomainService domainService;
	
	@Test
	public void testSynchonize() {
		domainService.synchronise();
	}

	@Autowired
	public void setDomainService(DomainService domainService) {
		this.domainService = domainService;
	}
	
}
