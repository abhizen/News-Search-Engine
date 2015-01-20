package edu.buffalo.cse.irf14.index;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

public class PostingsSizeComparator implements Comparator<String> {

	private final TreeMap<String, ArrayList<IndexPostings>> inputMap;

	public PostingsSizeComparator(
			final TreeMap<String, ArrayList<IndexPostings>> indexData) {
		this.inputMap = indexData;
	}

	@Override
	public int compare(String term1, String term2) {
		List<IndexPostings> postings1 = this.inputMap.get(term1);
		List<IndexPostings> postings2 = this.inputMap.get(term2);

		int totalTermFreq1 = 0;
		int totalTermFreq2 = 0;
		for (IndexPostings iP1 : postings1) {
			totalTermFreq1 = totalTermFreq1 + iP1.getTermFrequency();
		}
		for (IndexPostings iP2 : postings2) {
			totalTermFreq2 = totalTermFreq2 + iP2.getTermFrequency();
		}
		if (totalTermFreq1 > totalTermFreq2)
			return -1;
		return 1;
	}

}
