/**
 * ESUP-Portail Blank Application - Copyright (c) 2010 ESUP-Portail consortium.
 */
package org.esupportail.syncfsnx.domain;

import java.io.Serializable;

/**
 * @author Raymond Bourges
 * 
 */
public interface DomainService extends Serializable {
	
	/**
	 * synchronize a local folder with a remote section 
	 */
	void synchronise();
}
