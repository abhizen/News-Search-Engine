package edu.buffalo.cse.irf14.index;

import java.io.Serializable;

public class IndexPostings implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int documentIdOfTerm;

	private int termFrequency;

	public int getDocumentIdOfTerm() {
		return documentIdOfTerm;
	}

	public void setDocumentIdOfTerm(int documentIdOfTerm) {
		this.documentIdOfTerm = documentIdOfTerm;
	}

	public int getTermFrequency() {
		return termFrequency;
	}

	public void setTermFrequency(int termFrequency) {
		this.termFrequency = termFrequency;
	}

}
