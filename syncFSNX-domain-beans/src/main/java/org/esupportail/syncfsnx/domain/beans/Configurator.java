/**
 * 
 */
package org.esupportail.syncfsnx.domain.beans;

/**
 * @author bourges
 * An object to store all configurations
 */
public class Configurator {
	
	/**
	 * the path of the local folder to synchronize with nuxeo 
	 */
	String localPath;
	
	/**
	 * the path of the remote section in nuxeo (where local folder will be synchronized)
	 */
	String remotePath;
	
	/**
	 * The nuxeo URL for automation access
	 */
	String nuxeoAutomationURL;
	
	/**
	 * The nuxeo user name
	 */
	String user;
	
	/**
	 * the password for the nuxeo user
	 */
	String password;

	/**
	 * @return the localPath
	 */
	public String getLocalPath() {
		return localPath;
	}

	/**
	 * @param localPath the localPath to set
	 */
	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	/**
	 * @return the remotePath
	 */
	public String getRemotePath() {
		return remotePath;
	}

	/**
	 * @param remotePath the remotePath to set
	 */
	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the nuxeoAutomationURL
	 */
	public String getNuxeoAutomationURL() {
		return nuxeoAutomationURL;
	}

	/**
	 * @param nuxeoAutomationURL the nuxeoAutomationURL to set
	 */
	public void setNuxeoAutomationURL(String nuxeoAutomationURL) {
		this.nuxeoAutomationURL = nuxeoAutomationURL;
	}

}
