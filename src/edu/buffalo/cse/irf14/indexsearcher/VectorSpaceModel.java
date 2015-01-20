package edu.buffalo.cse.irf14.indexsearcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.buffalo.cse.irf14.index.IndexPostings;

public class VectorSpaceModel {
	private TreeMap<String, ArrayList<IndexPostings>> index;
	private Map<String, Integer> termDocFreqMap;
	private Map<String, Integer> postList;
	private String unparsedQuery;
	private String defOperator;
	private List<String> parsedQuery;
	private Map<String,Map<String, Integer>>  termPostList;

	public VectorSpaceModel(String query, String opr) {

		if (query == null || query.trim().equals(""))
			return;
		this.unparsedQuery = query;
		if (opr != null && !opr.equals(""))
			this.defOperator = opr;
		else
			this.defOperator = "AND";
		QueryIndex obj = new QueryIndex(query, defOperator);
		this.postList = obj.search();
		this.index = obj.getIndex();
		this.termDocFreqMap = new HashMap<String, Integer>();
		this.termPostList = obj.getTermPostList();
		this.parseQuery();
	}

	public int getDocFreqMap(String term) {
		
		int docFreq = 0;
		Map<String, Integer> indexList = termPostList.get(term);
		if(indexList!=null)
		{
			docFreq = indexList.size();
		}
		
		
		return docFreq;
	}

	public int getTermFreqForDoc(String DocId,String term){	
		int termFreq = 0;
		Map<String, Integer> indexList = termPostList.get(term);
		
		if(indexList!=null)
		{
			termFreq = 0;
			
			if (indexList.get(DocId) !=null)
				termFreq = termFreq + indexList.get(DocId);
		}
		
		return termFreq;
	}

	public Map<String, Double> getDocVector(String docId) {
		Map<String, Double> docVector = new HashMap<String, Double>();
		Map<String, Double> normDocVector = new HashMap<String, Double>();
		Map.Entry<String, Integer> entry = null;
		int docFreq = 0;
		int termFreq =0;
		double tfIdf = 0;
		int N = 10000;
		String term = null;

		Iterator<String> termItr= parsedQuery.listIterator();
		while(termItr.hasNext()){
			term = termItr.next();
			
			docFreq = getDocFreqMap(term);
			termFreq = getTermFreqForDoc(docId,term);
			if(docFreq>0)
				tfIdf = Math.log10(1 + termFreq)
					* Math.log10(N / docFreq);
			docVector.put(term, tfIdf);
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

		return normDocVector;
	}

	public Map<String, Double> getQueryScore() {
		String term = null;
		double freq = 0;
		
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
		
		double value = 0;
		double queryValue = 0;
		double docValue = 0;
		Iterator<Map.Entry<String, Integer>> postListItr = null;

		if (unparsedQuery == null || unparsedQuery.trim().length() < 1)
			return null;

		if (postList != null)
			postListItr = postList.entrySet().iterator();
		else
			return null;
		Map.Entry<String, Integer> postListEntry = null;
		while (postListItr.hasNext()) {
			postListEntry = postListItr.next();
			docVector = getDocVector(postListEntry.getKey());
			
			value = 0;
			for (String term : parsedQuery) {
				
				if (queryVector.get(term) == null)
					queryValue = 0;
				else
					queryValue = queryVector.get(term);

				if (docVector.get(term) != null)
					docValue = docVector.get(term);
				else
					docValue = 0;

				value = value + docValue * queryValue;
			}
			revScore.put(postListEntry.getKey(), value);
		}

		return revScore;
	}
    public void parseQuery()
    {
    	String term = null;
    	parsedQuery = new ArrayList<String>();
    	
    	for (String token : unparsedQuery.split(" ")) {
			term = getTerm(token);
			if(term!=null && term.trim().length()>0)
			parsedQuery.add(term);
    	}
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
		
		if (term.contains("["))
			term = term.replace("[", "");

		if (term.contains("}"))
			term = term.replace("}", "");
		
		if (term.contains("{"))
			term = term.replace("{", "");

		return term;
	}

}
