package edu.buffalo.cse.irf14.indexsearcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import edu.buffalo.cse.irf14.index.IndexPostings;

public class OkapiModel {
	private TreeMap<String, ArrayList<IndexPostings>> index;
	private Map<String, Integer> termDocFreqMap;
	private Map<String, Integer> postList;
	private String unparsedQuery;
	private String defOperator;
	private Map<String, Integer> wordCountPerFileMap;

	public OkapiModel(String query, String opr) {
		if (query == null || query.trim().equals(""))
			return;
		unparsedQuery = query;
		if (opr != null && opr.length() > 0)
			defOperator = opr;
		else
			defOperator = "OR";
		QueryIndex obj = new QueryIndex(query, defOperator);
		postList = obj.search();
		index = obj.getIndex();
		termDocFreqMap = new HashMap<String, Integer>();
		getDocFreqMap();
		WordCounter wordobj = new WordCounter();
		wordCountPerFileMap = wordobj.getWordCount();
	}

	public void getDocFreqMap() {
		String term;
		int docFreq;
		Map.Entry<String, ArrayList<IndexPostings>> currMapItem = null;
		Iterator<Map.Entry<String, ArrayList<IndexPostings>>> mapIterator = null;
		if (this.index != null && this.index.entrySet().size() > 0)
			mapIterator = this.index.entrySet().iterator();
		else
			return;
		while (mapIterator.hasNext()) {
			currMapItem = mapIterator.next();
			term = currMapItem.getKey().toString();
			docFreq = currMapItem.getValue().size();
			termDocFreqMap.put(term, docFreq);
		}
	}

	/**
	 * Method: This method returns frequency of all the terms in inverted index
	 * given document.
	 * 
	 * @return : Map<String,Integer>
	 * @param: Document for which term frequency to be found.
	 * */
	public Map<String, Integer> getTermFreqForDoc(String DocId) {
		String term = null;
		int termFreq;
		ArrayList<IndexPostings> list = null;
		Map<String, Integer> termFreqMap = new HashMap<String, Integer>();
		Map.Entry<String, ArrayList<IndexPostings>> currMapItem = null;
		Iterator<Map.Entry<String, ArrayList<IndexPostings>>> mapIterator = this.index
				.entrySet().iterator();
		while (mapIterator.hasNext()) {
			currMapItem = mapIterator.next();
			term = currMapItem.getKey();

			if (term == null || term.length() < 1)
				continue;

			list = currMapItem.getValue();
			termFreq = 0;
			for (IndexPostings token : list) {
				if (token.getDocumentIdOfTerm() == Integer.parseInt(DocId))
					termFreq = termFreq + token.getTermFrequency();
			}
			if (term != null && term.length() > 0)
				termFreqMap.put(term, termFreq);
		}

		return termFreqMap;
	}

	/***
	 * This document generates Tf-Idf vector for given docId.
	 * 
	 * @param docId
	 * @return Hashmap with key as term and value as normalized Tf-Idf the
	 *         document.
	 */
	public Map<String, Double> getDocVector(String docId) {
		Map<String, Double> docVector = new HashMap<String, Double>();
		Map<String, Double> normDocVector = new HashMap<String, Double>();
		Map<String, Integer> termFreq = getTermFreqForDoc(docId);
		Map.Entry<String, Integer> entry = null;
		int docFreq = 0;
		double tfIdf = 0;
		int N = 10000;
		int wordCount = 0;
		int wordCountAvg = getWordCountAvg();

		double k1 = 1.2;
		double b = 0.75;

		if (termFreq == null || termFreq.entrySet() == null)
			return null;

		Iterator<Map.Entry<String, Integer>> termItr = termFreq.entrySet()
				.iterator();
		while (termItr.hasNext()) {
			entry = termItr.next();
			Iterator<Map.Entry<String, Integer>> termDocItr = termDocFreqMap
					.entrySet().iterator();
			while (termDocItr.hasNext()) {
				Map.Entry<String, Integer> entryDocTerm = termDocItr.next();
				if (entryDocTerm.getKey().toLowerCase()
						.contains(entry.getKey().toLowerCase())) {
					wordCount = wordCountPerFileMap.get(docId);
					docFreq = termDocFreqMap.get(entry.getKey().toString());
					// tfIdf =
					// Math.log10(1+entry.getValue())*Math.log10(N/docFreq);
					tfIdf = Math.log10(N / docFreq)
							* ((1 + k1) * entry.getValue())
							/ ((k1 * ((1 - b) + b * (wordCount / wordCountAvg))) + entry
									.getValue());
					docVector.put(entry.getKey().toString(), tfIdf);
				}

			}
		}

		Iterator<Map.Entry<String, Double>> vsItr = docVector.entrySet()
				.iterator();
		Map.Entry<String, Double> entryVS = null;
		double normTfIdf = 0;
		double sum = 0;
		while (vsItr.hasNext()) {
			entryVS = vsItr.next();
			normTfIdf = entryVS.getValue();
			sum = sum + (normTfIdf * normTfIdf);
		}

		sum = Math.sqrt(sum);
		vsItr = null;
		vsItr = docVector.entrySet().iterator();

		while (vsItr.hasNext()) {

			entryVS = vsItr.next();
			normTfIdf = entryVS.getValue() / sum;
			normDocVector.put(entryVS.getKey().toString(), normTfIdf);
		}

		return docVector;
	}

	public Map<String, Double> getQueryScore() {
		String term = null;
		double freq = 0;
		// Iterator<Map.Entry<String,Integer>> termItr =
		// termFreq.entrySet().iterator();
		Map<String, Double> termMap = new HashMap<String, Double>();
		for (String token : unparsedQuery.split(" ")) {
			term = getTerm(token);
			if (!term.toLowerCase().equals("and")
					&& !term.toLowerCase().equals("or")
					&& !term.toLowerCase().equals("not")) {
				if (termMap != null && !termMap.isEmpty()
						&& termMap.containsKey(term)) {
					termMap.put(term, termMap.get(term) + 1);
				} else {
					freq = 1;
					termMap.put(term, freq);
				}
			}
		}

		Iterator<Map.Entry<String, Double>> termItr = termMap.entrySet()
				.iterator();
		Map.Entry<String, Double> entry = null;
		freq = 0;
		while (termItr.hasNext()) {
			entry = termItr.next();
			freq = freq + entry.getValue() * entry.getValue();
		}

		freq = Math.sqrt(freq);

		double sum = 0;

		Iterator<Map.Entry<String, Double>> termNormItr = termMap.entrySet()
				.iterator();
		while (termNormItr.hasNext()) {
			entry = termNormItr.next();
			sum = entry.getValue();
			sum = sum / freq;
			termMap.put(entry.getKey(), sum);
		}

		return termMap;
	}

	public Map<String, Double> getTotalScore() {
		Map<String, Double> queryVector = getQueryScore();
		Map<String, Double> docVector = null;
		Map<String, Double> revScore = new HashMap<String, Double>();
		String term = null;
		double value = 0;
		double queryValue = 0;
		double docValue = 0;
		Iterator<Map.Entry<String, Integer>> postListItr = null;

		if (postList != null)
			postListItr = postList.entrySet().iterator();
		else
			return null;

		Map.Entry<String, Integer> postListEntry = null;
		while (postListItr.hasNext()) {
			postListEntry = postListItr.next();
			docVector = getDocVector(postListEntry.getKey());

			// docVector.get(term);
			value = 0;
			for (String token : unparsedQuery.split(" ")) {
				term = getTerm(token);

				// docVector = getDocVector(postListEntry.getKey());

				if (queryVector.get(term) == null)
					queryValue = 0;
				else
					queryValue = queryVector.get(term);

				/*
				 * if (docVector.get(term) != null){ docValue =
				 * docVector.get(term);} else docValue = 0;
				 */
				docValue = 0;
				if (docVector != null) {
					Iterator<Map.Entry<String, Double>> mapIterator = docVector
							.entrySet().iterator();
					while (mapIterator.hasNext()) {
						Map.Entry<String, Double> mapItem = (Map.Entry<String, Double>) mapIterator
								.next();
						if (mapItem.getKey().trim()
								.equalsIgnoreCase(term.trim())
								|| mapItem.getKey().toLowerCase().trim()
										.contains(term.toLowerCase().trim())) {
							docValue = mapItem.getValue();
							// break;
						}
					}
				}

				value = value + docValue;
			}
			revScore.put(postListEntry.getKey(), value);
		}

		return revScore;
	}

	public String getTerm(String word) {
		String term;
		int index;

		if (word != null && word.length() > 0) {
			term = word;
		} else
			return null;

		if (term.contains(":")) {
			index = term.indexOf(":");
			term = term.substring(index + 1, term.length());
		}

		if (term.contains("<"))
			term = term.replace("<", "");

		if (term.contains(">"))
			term = term.replace(">", "");

		if (term.contains("]"))
			term = term.replace("]", "");

		if (term.contains("}"))
			term = term.replace("}", "");

		if (term.contains("{"))
			term = term.replace("{", "");

		return term;
	}

	public int getWordCountAvg() {
		int sum = 0;
		int index = 0;
		Iterator<Map.Entry<String, Integer>> itr = wordCountPerFileMap
				.entrySet().iterator();
		while (itr.hasNext()) {
			sum = sum + itr.next().getValue();
			index = index + 1;
		}

		sum = sum / index;
		return sum;
	}
}
