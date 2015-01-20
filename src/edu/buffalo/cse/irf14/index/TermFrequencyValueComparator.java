package edu.buffalo.cse.irf14.index;

import java.util.Comparator;
import java.util.Map;

public class TermFrequencyValueComparator implements Comparator<String> {

	private final Map<String, Integer> inputMap;

	public TermFrequencyValueComparator(final Map<String, Integer> indexData) {
		this.inputMap = indexData;
	}

	@Override
	public int compare(String term1, String term2) {

		int termFrequency1 = inputMap.get(term1);
		int termFrequency2 = inputMap.get(term2);

		if (termFrequency1 >= termFrequency2)
			return -1;

		return 1;
	}

}