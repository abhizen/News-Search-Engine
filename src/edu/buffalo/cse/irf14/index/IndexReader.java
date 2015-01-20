/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * @author nikhillo Class that emulates reading data back from a written index
 */
public class IndexReader {
	/**
	 * Default constructor
	 * 
	 * @param indexDir
	 *            : The root directory from which the index is to be read. This
	 *            will be exactly the same directory as passed on IndexWriter.
	 *            In case you make subdirectories etc., you will have to handle
	 *            it accordingly.
	 * @param type
	 *            The {@link IndexType} to read from
	 */

	private String indexDir;

	private IndexType type;

	private TreeMap<String, ArrayList<IndexPostings>> indexData;

	private TreeMap<Integer, String> docDictionary;

	@SuppressWarnings("unchecked")
	public IndexReader(String indexDir, IndexType type) {
		// TODO
		// Deserialize the java object here
		this.indexDir = indexDir;
		this.type = type;
		File indexDirectory = null;
		String directoryString = null;
		int sum = 0;
		try {
			if (indexDir != null) {
				// Deserialize Doc Dictionary
				File docDictionaryDirectory = new File(indexDir
						+ File.separator + "DocumentDictionary");
				FileInputStream fInStream0 = new FileInputStream(
						docDictionaryDirectory.getAbsolutePath());
				ObjectInputStream objInputStream0 = new ObjectInputStream(
						fInStream0);
				this.docDictionary = (TreeMap<Integer, String>) objInputStream0
						.readObject();
				objInputStream0.close();
				fInStream0.close();
				// Deserialize Indexes
				switch (type) {
				case TERM:
					directoryString = indexDir + File.separator + "term";
					break;
				case AUTHOR:
					directoryString = indexDir + File.separator + "author";
					break;
				case CATEGORY:
					directoryString = indexDir + File.separator + "category";
					break;
				case PLACE:
					directoryString = indexDir + File.separator + "place";
					break;
				default:
					break;
				}
				if (directoryString.contains("term")) {
					LinkedHashMap<String, ArrayList<IndexPostings>> fullTermMap = new LinkedHashMap<String, ArrayList<IndexPostings>>();
					for (int i = 0; i < 7; i++) {
						TreeMap<String, ArrayList<IndexPostings>> singleTermMap = new TreeMap<String, ArrayList<IndexPostings>>();
						switch (i) {
						case 0:
							indexDirectory = new File(directoryString
									+ "_symbol");
							break;
						case 1:
							indexDirectory = new File(directoryString + "_a_d");
							break;
						case 2:
							indexDirectory = new File(directoryString + "_e_h");
							break;
						case 3:
							indexDirectory = new File(directoryString + "_i_l");
							break;
						case 4:
							indexDirectory = new File(directoryString + "_m_p");
							break;
						case 5:
							indexDirectory = new File(directoryString + "_q_t");
							break;
						case 6:
							indexDirectory = new File(directoryString + "_u_z");
							break;

						}
						FileInputStream fInStream = new FileInputStream(
								indexDirectory.getAbsolutePath());
						ObjectInputStream objInputStream = new ObjectInputStream(
								fInStream);
						singleTermMap = (TreeMap<String, ArrayList<IndexPostings>>) objInputStream
								.readObject();
						objInputStream.close();
						fInStream.close();
						fullTermMap.putAll(singleTermMap);
					}
					this.indexData = new TreeMap<String, ArrayList<IndexPostings>>();
					sum = sum + this.indexData.size();
					System.out.println("Number of terms " + sum);
					this.indexData.putAll(fullTermMap);

				} else {
					indexDirectory = new File(directoryString);
					FileInputStream fInStream = new FileInputStream(
							indexDirectory.getAbsolutePath());
					ObjectInputStream objInputStream = new ObjectInputStream(
							fInStream);
					this.indexData = (TreeMap<String, ArrayList<IndexPostings>>) objInputStream
							.readObject();
					objInputStream.close();
					fInStream.close();
					fInStream.close();
				}
			} else {
				throw new FileNotFoundException();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out
					.println("IndexReader Error: The file you are trying to lookup is not correct / available.");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out
					.println("IndexReader Error: IO Error has occured while tryign to reach your file.");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.out
					.println("IndexReader Error: ClassNotFound while Deserializing Java Object from file.");
			e.printStackTrace();
		}

	}

	/**
	 * Get total number of terms from the "key" dictionary associated with this
	 * index. A postings list is always created against the "key" dictionary
	 * 
	 * @return The total number of terms
	 */
	public int getTotalKeyTerms() {
		int keyCount = 0;
		if (this.indexData != null) {
			keyCount = this.indexData.size();
			// part to be commented while code submission - starts

			Iterator<Map.Entry<String, ArrayList<IndexPostings>>> mapIterator = this.indexData
					.entrySet().iterator();
			while (mapIterator.hasNext()) {
				Map.Entry<String, ArrayList<IndexPostings>> mapItem = (Map.Entry<String, ArrayList<IndexPostings>>) mapIterator
						.next();
				System.out.println(mapItem.getKey());
			}
			System.out.println("Total Key Terms: " + keyCount);

			// part to be commented while code submission - ends
		}
		return keyCount;
	}

	/**
	 * Get total number of terms from the "value" dictionary associated with
	 * this index. A postings list is always created with the "value" dictionary
	 * 
	 * @return The total number of terms
	 */
	public int getTotalValueTerms() {
		int valueCount = 0;
		if (this.indexData != null) {
			Iterator<Map.Entry<String, ArrayList<IndexPostings>>> mapIterator = this.indexData
					.entrySet().iterator();
			List<Integer> uniqueDocIdsInIndex = new ArrayList<Integer>();
			while (mapIterator.hasNext()) {
				Map.Entry<String, ArrayList<IndexPostings>> mapItem = (Map.Entry<String, ArrayList<IndexPostings>>) mapIterator
						.next();
				// valueCount = valueCount + mapItem.getValue().size();
				for (IndexPostings ip : mapItem.getValue()) {
					/*
					 * System.out.println("#####" + mapItem.getKey() + ": " +
					 * ip.getDocumentIdOfTerm());
					 */
					int flag = 0;
					for (Integer ud : uniqueDocIdsInIndex) {
						if (ip.getDocumentIdOfTerm() == ud) {
							flag = 1;
							break;
						}
					}
					if (flag == 0) {
						uniqueDocIdsInIndex.add(ip.getDocumentIdOfTerm());
					}
				}
			}
			/*
			 * System.out.println("Total Value Terms: " +
			 * uniqueDocIdsInIndex.size());
			 */
			valueCount = uniqueDocIdsInIndex.size();
		}
		return valueCount;
	}

	/**
	 * Method to get the postings for a given term. You can assume that the raw
	 * string that is used to query would be passed through the same Analyzer as
	 * the original field would have been.
	 * 
	 * @param term
	 *            : The "analyzed" term to get postings for
	 * @return A Map containing the corresponding fileid as the key and the
	 *         number of occurrences as values if the given term was found, null
	 *         otherwise.
	 */
	public Map<String, Integer> getPostings(String term) {
		Map<String, Integer> postingsList = null;
		int termFreq =0;
		if (this.indexData != null) {
			Iterator<Map.Entry<String, ArrayList<IndexPostings>>> mapIterator = this.indexData
					.entrySet().iterator();
			postingsList = new HashMap<String, Integer>();
			while (mapIterator.hasNext()) {
				Map.Entry<String, ArrayList<IndexPostings>> mapItem = (Map.Entry<String, ArrayList<IndexPostings>>) mapIterator
						.next();
				if (mapItem.getKey().trim().equalsIgnoreCase(term.trim()) || mapItem.getKey().toLowerCase().trim().contains(term.toLowerCase().trim())) {
					for (IndexPostings iP : mapItem.getValue()) {
						if(postingsList.containsKey(iP.getDocumentIdOfTerm()))
							termFreq = postingsList.get(iP.getDocumentIdOfTerm());
						else
							termFreq = 0;
						
						 if (termFreq!=0)
							 termFreq = termFreq + iP.getTermFrequency();
						 else
							 termFreq = iP.getTermFrequency();
						 
						postingsList.put(
								docDictionary.get(iP.getDocumentIdOfTerm()),
								termFreq);
					}
					//break;
				}
			}
		}
		return postingsList;
	}

	/**
	 * Method to get the top k terms from the index in terms of the total number
	 * of occurrences.
	 * 
	 * @param k
	 *            : The number of terms to fetch
	 * @return : An ordered list of results. Must be <=k fr valid k values null
	 *         for invalid k values
	 */
	public List<String> getTopK(int k) {
		if (k < 1 || this.indexData == null)
			return null;
		List<String> topKList = new ArrayList<String>();
		TreeMap<String, ArrayList<IndexPostings>> sortedByPostingsSizeMap = new TreeMap<String, ArrayList<IndexPostings>>(
				new PostingsSizeComparator(this.indexData));
		sortedByPostingsSizeMap.putAll(this.indexData);
		Iterator<Map.Entry<String, ArrayList<IndexPostings>>> mapIterator = sortedByPostingsSizeMap
				.entrySet().iterator();
		int i = 0;
		while (mapIterator.hasNext() && i < k) {
			Map.Entry<String, ArrayList<IndexPostings>> mapItem = (Map.Entry<String, ArrayList<IndexPostings>>) mapIterator
					.next();
			topKList.add(mapItem.getKey());
			i++;
		}
		return topKList;
	}

	/**
	 * Method to implement a simple boolean AND query on the given index
	 * 
	 * @param terms
	 *            The ordered set of terms to AND, similar to getPostings() the
	 *            terms would be passed through the necessary Analyzer.
	 * @return A Map (if all terms are found) containing FileId as the key and
	 *         number of occurrences as the value, the number of occurrences
	 *         would be the sum of occurrences for each participating term.
	 *         return null if the given term list returns no results BONUS ONLY
	 */
	public Map<String, Integer> query(String... terms) {
		if (this.indexData == null)
			return null;
		// STEP 1: SEND THE STRINGS THROUGH THE ANALYZER FIRST, BEFORE WE
		// PROCEED WITH THE INDEX EXTRACTION.
		TreeMap<String, ArrayList<IndexPostings>> queriedPostings = new TreeMap<String, ArrayList<IndexPostings>>();

		// STEP 2: Retrieve the terms and their corresponding postings and save
		// in a POSTINGS MAP "queriedPostings"
		Iterator<Map.Entry<String, ArrayList<IndexPostings>>> mapIterator = this.indexData
				.entrySet().iterator();
		while (mapIterator.hasNext()) {
			Map.Entry<String, ArrayList<IndexPostings>> mapItem = (Map.Entry<String, ArrayList<IndexPostings>>) mapIterator
					.next();
			for (String term : terms) {
				if (mapItem.getKey().equals(term)) {
					queriedPostings.put(mapItem.getKey(), mapItem.getValue());
					break;
				}
			}
		}
		// STEP 3: Sort the POSTINGS MAP "queriedPostings" based on the size of
		// the postings. Resultant map = "reverseSortedMap"
		TreeMap<String, ArrayList<IndexPostings>> sortedByPostingsSizeMap = new TreeMap<String, ArrayList<IndexPostings>>(
				new PostingsSizeComparator(queriedPostings));
		sortedByPostingsSizeMap.putAll(queriedPostings);
		NavigableMap<String, ArrayList<IndexPostings>> reverseSortedMap = sortedByPostingsSizeMap
				.descendingMap();

		// STEP 4: Now perform a boolean "AND" and update the 'common' postings
		// in a NEW MAP - finalBooleanMap
		// also, keep count of term frequency seperately

		// STEP 4.1: Retrieve the DocIds that are common in all the postings.
		// Resultant = "ArrayList<Integer> smallestPostingDocIds"
		mapIterator = reverseSortedMap.entrySet().iterator();
		// 1st we get the smallest arraylist of posting ids... this wud be the
		// first element in the map.
		Map.Entry<String, ArrayList<IndexPostings>> firstAndShortestMapItem = null;
		ArrayList<Integer> smallestPostingDocIds = new ArrayList<Integer>();
		if (mapIterator.hasNext()) {
			firstAndShortestMapItem = (Map.Entry<String, ArrayList<IndexPostings>>) mapIterator
					.next();
			for (IndexPostings iP : firstAndShortestMapItem.getValue()) {
				smallestPostingDocIds.add(iP.getDocumentIdOfTerm());
			}
		}
		Map.Entry<String, ArrayList<IndexPostings>> currentMapItem = null;
		while (mapIterator.hasNext()) {
			int i = 0;
			int j = 0;
			ArrayList<Integer> newPostingsOfDocIds = new ArrayList<Integer>();
			currentMapItem = (Map.Entry<String, ArrayList<IndexPostings>>) mapIterator
					.next();
			ArrayList<IndexPostings> currentPostings = currentMapItem
					.getValue();
			while (i < smallestPostingDocIds.size()
					&& j < currentPostings.size()) {
				if (smallestPostingDocIds.get(i) == currentPostings.get(j)
						.getDocumentIdOfTerm()) {
					newPostingsOfDocIds.add(currentPostings.get(j)
							.getDocumentIdOfTerm());
					i++;
					j++;
				} else if (smallestPostingDocIds.get(i) < currentPostings
						.get(j).getDocumentIdOfTerm())
					i++;
				else
					j++;
			}
			smallestPostingDocIds = new ArrayList<Integer>();
			for (Integer id : newPostingsOfDocIds) {
				smallestPostingDocIds.add(id);
			}
		}
		Map<String, Integer> finalResult = new HashMap<String, Integer>();

		// STEP 4.2: now update the sortedByPostingsSizeMap for only the docIds
		// (newPostingsOfDocIds) that are common.
		Map.Entry<String, ArrayList<IndexPostings>> mapItem;
		for (Integer docId : smallestPostingDocIds) {
			int termFreqCount = 0;
			mapIterator = queriedPostings.entrySet().iterator();
			while (mapIterator.hasNext()) {
				mapItem = (Map.Entry<String, ArrayList<IndexPostings>>) mapIterator
						.next();
				for (IndexPostings iP : mapItem.getValue()) {
					if (docId == iP.getDocumentIdOfTerm()) {
						termFreqCount = termFreqCount + iP.getTermFrequency();
						break;
					}
				}
			}
			finalResult.put(docDictionary.get(docId), termFreqCount);
		}
		Map<String, Integer> termFreqSortedResult = new TreeMap<String, Integer>(
				new TermFrequencyValueComparator(finalResult));
		termFreqSortedResult.putAll(finalResult);
		Map<String, Integer> finalMap = new LinkedHashMap<String, Integer>();
		finalMap.putAll(termFreqSortedResult);
		if (finalMap.size() == 0)
			return null;
		return finalMap;
	}

	public TreeMap<String, ArrayList<IndexPostings>> getIndex() {
		return indexData;
	}
}
