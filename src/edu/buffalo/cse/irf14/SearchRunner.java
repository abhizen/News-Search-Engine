package edu.buffalo.cse.irf14;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.buffalo.cse.irf14.indexsearcher.OkapiModel;
import edu.buffalo.cse.irf14.indexsearcher.VectorSpaceModel;

/**
 * Main class to run the searcher. As before implement all TODO methods unless
 * marked for bonus
 * 
 * @author nikhillo
 * 
 */
public class SearchRunner {
	public enum ScoringModel {
		TFIDF, OKAPI
	};

	String curIndexDir;
	String curCorpusDir;
	char curMode;
	PrintStream curStream;
	String queryNum;
	StringBuilder queryOutputBuilder;
	int printFlag;

	/**
	 * Default (and only public) constuctor
	 * 
	 * @param indexDir
	 *            : The directory where the index resides
	 * @param corpusDir
	 *            : Directory where the (flattened) corpus resides
	 * @param mode
	 *            : Mode, one of Q or E
	 * @param stream
	 *            : Stream to write output to
	 */
	public SearchRunner(String indexDir, String corpusDir, char mode,
			PrintStream stream) {
		// TODO: IMPLEMENT THIS METHOD
		if (indexDir != null && indexDir.length() > 0)
			curIndexDir = indexDir;

		if (corpusDir != null && corpusDir.length() > 0)
			curCorpusDir = corpusDir;

		if (mode != '\0')
			curMode = mode;

		if (stream != null)
			curStream = stream;

		queryNum = null;

		queryOutputBuilder = new StringBuilder();
		printFlag = 0;
	}

	/**
	 * Method to execute given query in the Q mode
	 * 
	 * @param userQuery
	 *            : Query to be parsed and executed
	 * @param model
	 *            : Scoring Model to use for ranking results
	 */
	public void query(String userQuery, ScoringModel model) {
		// TODO: IMPLEMENT THIS METHOD
		String query = null;
		VectorSpaceModel obj = null;
		OkapiModel obj1 = null;
		Map<String, Double> scoreMap = null;
		Map.Entry<String, Double> entry = null;
		String snippet = null;
		int firstComma = 0;

		try {
			if (userQuery != null && userQuery.length() > 0)
				query = userQuery;

			if (model.equals(ScoringModel.TFIDF)) {
				obj = new VectorSpaceModel(query, "AND");
				int index = 0;
				firstComma = 0;
				if (obj == null)
					return;
				scoreMap = obj.getTotalScore();
				if (scoreMap == null || scoreMap.entrySet() == null)
					return;
				Map<String, Double> revSortedScoreMap = sortByValues(scoreMap);
				Iterator<Map.Entry<String, Double>> itr = revSortedScoreMap
						.entrySet().iterator();
				if (revSortedScoreMap != null && !revSortedScoreMap.isEmpty()) {
					if (revSortedScoreMap.entrySet().size() > 0)
						queryOutputBuilder.append(queryNum + ":{");
					while (itr.hasNext() && index < 10) {
						entry = itr.next();
						if (firstComma > 0) {
							// this.curStream.print(",\t");
							queryOutputBuilder.append(",");
						}
						queryOutputBuilder.append(entry.getKey() + "#"
								+ entry.getValue());
						snippet = getSnnippets(entry.getKey());
						if (snippet != null && snippet.length() > 0)
							queryOutputBuilder.append(" " + snippet + "\n");
						firstComma++;
						index++;
					}
					// this.curStream.print("}\n");
					queryOutputBuilder.append("}\n");
				}

			}

			if (model.equals(ScoringModel.OKAPI)) {
				obj1 = new OkapiModel(query, "OR");
				int index = 0;
				firstComma = 0;
				scoreMap = obj1.getTotalScore();
				if (scoreMap == null || scoreMap.entrySet() == null)
					return;
				Map<String, Double> revSortedScoreMap = sortByValues(scoreMap);
				Iterator<Map.Entry<String, Double>> itr = revSortedScoreMap
						.entrySet().iterator();
				if (revSortedScoreMap != null && !revSortedScoreMap.isEmpty()) {
					if (revSortedScoreMap.entrySet().size() > 0)
						queryOutputBuilder.append(queryNum + ":{");

					while (itr.hasNext() && index < 10) {
						entry = itr.next();
						if (firstComma > 0) {
							queryOutputBuilder.append(",");
						}
						/*
						 * this.curStream.println(entry.getKey() + "#" +
						 * entry.getValue() + ",");
						 */
						queryOutputBuilder.append(entry.getKey() + "#"
								+ entry.getValue() + ",");
						snippet = getSnnippets(entry.getKey());
						if (snippet != null && snippet.length() > 0)
							// this.curStream.println(snippet + "\n");
							queryOutputBuilder.append(" " + snippet + "\n");
						firstComma++;
						index++;

					}
					// this.curStream.print("}\n");
					queryOutputBuilder.append("}\n");
				}
			}
			if (printFlag == 0)
				this.curStream.append(queryOutputBuilder.toString());
		} catch (Exception e) {
			System.out.println("Unexpected error caught in query");
		}
		return;

	}

	/**
	 * Method to execute queries in E mode
	 * 
	 * @param queryFile
	 *            : The file from which queries are to be read and executed
	 */
	public void query(File queryFile) {
		// TODO: IMPLEMENT THIS METHOD
		String query = null;
		String userQuery = null;
		// String queryNum = null;
		int index = 0;
		int numQuery = 0;

		BufferedReader bufferReader = null;
		try {
			bufferReader = new BufferedReader(new FileReader(queryFile));
			while ((query = bufferReader.readLine()) != null) {
				printFlag = 1;
				userQuery = query;
				index = query.indexOf(":");
				if (index < 0)
					break;
				queryNum = userQuery.substring(0, index);
				userQuery = userQuery.substring(index + 1, userQuery.length());
				query(userQuery, ScoringModel.TFIDF);
				numQuery++;
			}
			if (numQuery > 0) {
				queryOutputBuilder.insert(0, "numResults=" + numQuery + "\n");
				// this.curStream.
				this.curStream.println(queryOutputBuilder.toString());
			}
			printFlag = 0;
		} catch (FileNotFoundException e1) {
			System.out.println("FNF Exception caught in query");
		} catch (IOException e) {
			System.out.println("IO Exception caught in query");
		} catch (Exception e) {
			System.out.println("Unexpected error caught in query");
		} finally {
			if (bufferReader != null) {
				try {
					bufferReader.close();
				} catch (IOException e) {
					System.out
							.println("IO Exception caught in query's finally block");
				}
			}
		}
	}

	/**
	 * General cleanup method
	 */
	public void close() {
		if (curStream != null)
			curStream.close();
	}

	/**
	 * Method to indicate if wildcard queries are supported
	 * 
	 * @return true if supported, false otherwise
	 */
	public static boolean wildcardSupported() {
		// TODO: CHANGE THIS TO TRUE ONLY IF WILDCARD BONUS ATTEMPTED
		return false;
	}

	/**
	 * Method to get substituted query terms for a given term with wildcards
	 * 
	 * @return A Map containing the original query term as key and list of
	 *         possible expansions as values if exist, null otherwise
	 */
	public Map<String, List<String>> getQueryTerms() {
		// TODO:IMPLEMENT THIS METHOD IFF WILDCARD BONUS ATTEMPTED
		return null;

	}

	/**
	 * Method to indicate if speel correct queries are supported
	 * 
	 * @return true if supported, false otherwise
	 */
	public static boolean spellCorrectSupported() {
		// TODO: CHANGE THIS TO TRUE ONLY IF SPELLCHECK BONUS ATTEMPTED
		return false;
	}

	/**
	 * Method to get ordered "full query" substitutions for a given misspelt
	 * query
	 * 
	 * @return : Ordered list of full corrections (null if none present) for the
	 *         given query
	 */
	public List<String> getCorrections() {
		// TODO: IMPLEMENT THIS METHOD IFF SPELLCHECK EXECUTED
		return null;
	}

	private Map<String, Double> sortByValues(Map<String, Double> map) {
		List<Map.Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>(
				map.entrySet());

		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
			@Override
			public int compare(Map.Entry<String, Double> e1,
					Map.Entry<String, Double> e2) {

				double v1 = (double) e1.getValue();
				double v2 = (double) e2.getValue();

				if (v1 > v2)
					return -1;
				else if (v1 < v2)
					return 1;
				else
					return 0;
			}
		});

		Map<String, Double> reverseSortedMap = new LinkedHashMap<String, Double>();
		for (Map.Entry<String, Double> e : list) {
			reverseSortedMap.put(e.getKey(), e.getValue());
		}

		return reverseSortedMap;
	}

	private String getSnnippets(String docId) {
		File inputFiles = new File(curCorpusDir);
		String[] files = inputFiles.list();
		String fileName = null;
		String line = null;
		BufferedReader bufferReader = null;
		int index = 0;
		try {
			for (String file : files) {
				fileName = inputFiles.getAbsolutePath() + File.separator + file;
				if (file.equals(docId)) {
					bufferReader = new BufferedReader(new FileReader(fileName));

					StringBuilder contentBuilder = new StringBuilder();
					while ((line = bufferReader.readLine()) != null) {
						if (line.trim().length() > 0) {
							contentBuilder.append(line);
							contentBuilder.append(" ");
							index++;
						}
						if (index > 2)
							break;
					}
					if (contentBuilder != null && contentBuilder.length() > 0)
						line = contentBuilder.toString();
					break;
				}
			}
		} catch (FileNotFoundException e1) {
			System.out.println("FNF Exception caught in getSnippets");
		} catch (IOException e) {
			System.out.println("IO Exception caught in getSnippets");
		} catch (Exception e) {
			System.out.println("Unexpected error caught in getSnippets");
		} finally {
			if (bufferReader != null) {
				try {
					bufferReader.close();
				} catch (IOException e) {
					System.out
							.println("IO Exception caught in getSnippets's finally block");
				}
			}
		}
		return line;
	}
}
