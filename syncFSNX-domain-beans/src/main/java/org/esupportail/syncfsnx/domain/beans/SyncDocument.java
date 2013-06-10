/**
 * 
 */
package org.esupportail.syncfsnx.domain.beans;

import java.util.Date;

/**
 * @author bourges
 * Document manipulated during synchronization
 */
public class SyncDocument {
	
	String relativePath;
	
	Date modificationDate;
	
	SyncDocumentType syncDocumentType;

	/**
	 * @return the relativePath
	 */
	public String getRelativePath() {
		return relativePath;
	}

	/**
	 * @param relativePath the relativePath to set
	 */
	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}

	/**
	 * @return the modificationDate
	 */
	public Date getModificationDate() {
		return modificationDate;
	}

	/**
	 * @param modificationDate the modificationDate to set
	 */
	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	/**
	 * @return the documentType
	 */
	public SyncDocumentType getDocumentType() {
		return syncDocumentType;
	}

	/**
	 * @param documentType the documentType to set
	 */
	public void setDocumentType(SyncDocumentType documentType) {
		this.syncDocumentType = documentType;
	}
	
	public boolean isFolder() {
		return syncDocumentType.equals(SyncDocumentType.FOLDER);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((modificationDate == null) ? 0 : modificationDate.hashCode());
		result = prime * result
				+ ((relativePath == null) ? 0 : relativePath.hashCode());
		result = prime
				* result
				+ ((syncDocumentType == null) ? 0 : syncDocumentType.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SyncDocument other = (SyncDocument) obj;
		if (modificationDate == null) {
			if (other.modificationDate != null)
				return false;
		} else if (!modificationDate.equals(other.modificationDate))
			return false;
		if (relativePath == null) {
			if (other.relativePath != null)
				return false;
		} else if (!relativePath.equals(other.relativePath))
			return false;
		if (syncDocumentType != other.syncDocumentType)
			return false;
		return true;
	}

}
