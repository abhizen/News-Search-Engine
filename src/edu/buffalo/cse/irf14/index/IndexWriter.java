/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;
import java.util.HashMap;

import edu.buffalo.cse.irf14.analysis.AnalyzerFactory;
import edu.buffalo.cse.irf14.analysis.FieldChainedAnalyzer;
import edu.buffalo.cse.irf14.analysis.Token;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;
import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;
import edu.buffalo.cse.irf14.threads.IndexWriterRunnable;

/**
 * @author nikhillo Class responsible for writing indexes to disk
 * @param <E>
 */
public class IndexWriter<E> {

	private String indexDir;
	private int counter;
	private static HashMap<Integer, String> docDictionary = new HashMap<Integer, String>();
	/*private static TreeMap<StringC> a_d_termIndex = new TreeMap<String, ArrayList<IndexPostings>>();

	private static TreeMap<String, ArrayList<IndexPostings>> e_h_termIndex = new TreeMap<String, ArrayList<IndexPostings>>();

	private static TreeMap<String, ArrayList<IndexPostings>> i_l_termIndex = new TreeMap<String, ArrayList<IndexPostings>>();

	private static TreeMap<String, ArrayList<IndexPostings>> m_p_termIndex = new TreeMap<String, ArrayList<IndexPostings>>();

	private static TreeMap<String, ArrayList<IndexPostings>> q_t_termIndex = new TreeMap<String, ArrayList<IndexPostings>>();

	private static TreeMap<String, ArrayList<IndexPostings>> u_z_termIndex = new TreeMap<String, ArrayList<IndexPostings>>();

	private static TreeMap<String, ArrayList<IndexPostings>> symbol_termIndex = new TreeMap<String, ArrayList<IndexPostings>>();

	private static TreeMap<String, ArrayList<IndexPostings>> authorIndex = new TreeMap<String, ArrayList<IndexPostings>>();
	private static TreeMap<String, ArrayList<IndexPostings>> categoryIndex = new TreeMap<String, ArrayList<IndexPostings>>();
	private static TreeMap<String, ArrayList<IndexPostings>> placeIndex = new TreeMap<String, ArrayList<IndexPostings>>();*/
	
	private static HashMap<String, ArrayList<IndexPostings>> a_d_termIndex = new HashMap<String, ArrayList<IndexPostings>>();
	private static HashMap<String,ArrayList<IndexPostings>> e_h_termIndex = new HashMap<String, ArrayList<IndexPostings>>();
	private static HashMap<String,ArrayList<IndexPostings>> i_l_termIndex = new HashMap<String, ArrayList<IndexPostings>>();
	private static HashMap<String, ArrayList<IndexPostings>> m_p_termIndex = new HashMap<String, ArrayList<IndexPostings>>();
	private static HashMap<String, ArrayList<IndexPostings>> q_t_termIndex = new HashMap<String, ArrayList<IndexPostings>>();
	private static HashMap<String, ArrayList<IndexPostings>> u_z_termIndex = new HashMap<String, ArrayList<IndexPostings>>();
	private static HashMap<String, ArrayList<IndexPostings>> symbol_termIndex = new HashMap<String, ArrayList<IndexPostings>>();
	private static HashMap<String, ArrayList<IndexPostings>> authorIndex = new HashMap<String, ArrayList<IndexPostings>>();
	private static HashMap<String, ArrayList<IndexPostings>> categoryIndex = new HashMap<String, ArrayList<IndexPostings>>();
	private static HashMap<String, ArrayList<IndexPostings>> placeIndex = new HashMap<String, ArrayList<IndexPostings>>();

	
	private static TokenStream tokenStream;
	public static long time =0;

	/**
	 * Default constructor
	 * 
	 * @param indexDir
	 *            : The root directory to be sued for indexing
	 */

	public IndexWriter(String indexDir) {
		this.indexDir = indexDir;
		this.counter = 0;
	}

	public void createTermIndexAndPlaceInProperMap(
			HashMap<String, ArrayList<IndexPostings>> index, Token t, int docId) {
		ArrayList<IndexPostings> docsTokenIsIn = null;
		int termFrequencyCount = 0;
		if (index.get(t.getTermText()) != null) {
			docsTokenIsIn = index.get(t.getTermText());
			int flag = 0;
			int postingCount = 0;
			for (IndexPostings posting : docsTokenIsIn) {
				if (posting.getDocumentIdOfTerm() == docId) {
					termFrequencyCount = posting.getTermFrequency() + 1;
					posting.setTermFrequency(termFrequencyCount);
					docsTokenIsIn.set(postingCount, posting);
					flag = 1;
					break;
				}
				postingCount++;
			}
			if (flag == 0) {
				IndexPostings iP = new IndexPostings();
				iP.setDocumentIdOfTerm(docId);
				iP.setTermFrequency(1);
				docsTokenIsIn.add(iP);
			}
			index.put(t.getTermText(), docsTokenIsIn);
		} else {
			IndexPostings iP = new IndexPostings();
			iP.setDocumentIdOfTerm(docId);
			iP.setTermFrequency(1);
			docsTokenIsIn = new ArrayList<IndexPostings>();
			docsTokenIsIn.add(iP);
			index.put(t.getTermText(), docsTokenIsIn);
		}
	}

	/**
	 * Method to add the given Document to the index This method should take
	 * care of reading the filed values, passing them through corresponding
	 * analyzers and then indexing the results for each indexable field within
	 * the document.
	 * 
	 * @param d
	 *            : The Document to be added
	 * @throws IndexerException
	 *             : In case any error occurs
	 */
	public void addDocument(Document d) throws IndexerException {

		try {
			
			//System.out.println(counter++);
			/*if (counter == 164)
				System.out.println(counter);*/
			if (this.indexDir != null) {
				// Create Document Dictionary
				docDictionary.put(new Integer(docDictionary.size() + 1),
						d.getField(FieldNames.FILEID)[0]);
				int docId = docDictionary.size();

				// Retrieve tokens for content && Create Term Index

				TokenStream contentTokenStream = null;
				HashMap<String, ArrayList<IndexPostings>> index = null;
				
				long startTime1 = System.nanoTime();
				
				if (d.getField(FieldNames.CONTENT) != null) {
					contentTokenStream = this.retrieveTokenStream(
							d.getField(FieldNames.CONTENT)[0],
							FieldNames.CONTENT);
					
					long endTime1 = System.nanoTime();
					//System.out.println("Took "+(endTime1 - startTime1) + " ns");
					// ################
					if (contentTokenStream != null
							&& contentTokenStream.getList() != null) {
						for (Token t : contentTokenStream.getList()) {

							if (t.getTermText().toLowerCase().startsWith("a")
									|| t.getTermText().toLowerCase()
											.startsWith("b")
									|| t.getTermText().toLowerCase()
											.startsWith("c")
									|| t.getTermText().toLowerCase()
											.startsWith("d")) {
								createTermIndexAndPlaceInProperMap(
										IndexWriter.a_d_termIndex, t, docId);
							} else if (t.getTermText().toLowerCase()
									.startsWith("e")
									|| t.getTermText().toLowerCase()
											.startsWith("f")
									|| t.getTermText().toLowerCase()
											.startsWith("g")
									|| t.getTermText().toLowerCase()
											.startsWith("h")) {
								createTermIndexAndPlaceInProperMap(
										IndexWriter.e_h_termIndex, t, docId);
							} else if (t.getTermText().toLowerCase()
									.startsWith("i")
									|| t.getTermText().toLowerCase()
											.startsWith("j")
									|| t.getTermText().toLowerCase()
											.startsWith("k")
									|| t.getTermText().toLowerCase()
											.startsWith("l")) {
								createTermIndexAndPlaceInProperMap(
										IndexWriter.i_l_termIndex, t, docId);
							} else if (t.getTermText().toLowerCase()
									.startsWith("m")
									|| t.getTermText().toLowerCase()
											.startsWith("n")
									|| t.getTermText().toLowerCase()
											.startsWith("o")
									|| t.getTermText().toLowerCase()
											.startsWith("p")) {
								createTermIndexAndPlaceInProperMap(
										IndexWriter.m_p_termIndex, t, docId);
							} else if (t.getTermText().toLowerCase()
									.startsWith("q")
									|| t.getTermText().toLowerCase()
											.startsWith("r")
									|| t.getTermText().toLowerCase()
											.startsWith("s")
									|| t.getTermText().toLowerCase()
											.startsWith("t")) {
								createTermIndexAndPlaceInProperMap(
										IndexWriter.q_t_termIndex, t, docId);
							} else if (t.getTermText().toLowerCase()
									.startsWith("u")
									|| t.getTermText().toLowerCase()
											.startsWith("v")
									|| t.getTermText().toLowerCase()
											.startsWith("w")
									|| t.getTermText().toLowerCase()
											.startsWith("x")
									|| t.getTermText().toLowerCase()
											.startsWith("y")
									|| t.getTermText().toLowerCase()
											.startsWith("z")) {
								createTermIndexAndPlaceInProperMap(
										IndexWriter.u_z_termIndex, t, docId);
							} else {
								createTermIndexAndPlaceInProperMap(
										IndexWriter.symbol_termIndex, t, docId);
							}
						}
					}
				}
				// ###################

				// Retrieve tokens for TITLE and add to TERM Index.. Need to add
				// a BOOST here
				TokenStream titleTokenStream = null;
				
				if (d.getField(FieldNames.TITLE) != null) {
					titleTokenStream = this.retrieveTokenStream(
							d.getField(FieldNames.TITLE)[0], FieldNames.TITLE);
					
					
					if (titleTokenStream != null
							&& titleTokenStream.getList() != null) {
						for (Token t : titleTokenStream.getList()) {

							if (t.getTermText().toLowerCase().startsWith("a")
									|| t.getTermText().toLowerCase()
											.startsWith("b")
									|| t.getTermText().toLowerCase()
											.startsWith("c")
									|| t.getTermText().toLowerCase()
											.startsWith("d")) {
								createTermIndexAndPlaceInProperMap(
										IndexWriter.a_d_termIndex, t, docId);
							} else if (t.getTermText().toLowerCase()
									.startsWith("e")
									|| t.getTermText().toLowerCase()
											.startsWith("f")
									|| t.getTermText().toLowerCase()
											.startsWith("g")
									|| t.getTermText().toLowerCase()
											.startsWith("h")) {
								createTermIndexAndPlaceInProperMap(
										IndexWriter.e_h_termIndex, t, docId);
							} else if (t.getTermText().toLowerCase()
									.startsWith("i")
									|| t.getTermText().toLowerCase()
											.startsWith("j")
									|| t.getTermText().toLowerCase()
											.startsWith("k")
									|| t.getTermText().toLowerCase()
											.startsWith("l")) {
								createTermIndexAndPlaceInProperMap(
										IndexWriter.i_l_termIndex, t, docId);
							} else if (t.getTermText().toLowerCase()
									.startsWith("m")
									|| t.getTermText().toLowerCase()
											.startsWith("n")
									|| t.getTermText().toLowerCase()
											.startsWith("o")
									|| t.getTermText().toLowerCase()
											.startsWith("p")) {
								createTermIndexAndPlaceInProperMap(
										IndexWriter.m_p_termIndex, t, docId);
							} else if (t.getTermText().toLowerCase()
									.startsWith("q")
									|| t.getTermText().toLowerCase()
											.startsWith("r")
									|| t.getTermText().toLowerCase()
											.startsWith("s")
									|| t.getTermText().toLowerCase()
											.startsWith("t")) {
								createTermIndexAndPlaceInProperMap(
										IndexWriter.q_t_termIndex, t, docId);
							} else if (t.getTermText().toLowerCase()
									.startsWith("u")
									|| t.getTermText().toLowerCase()
											.startsWith("v")
									|| t.getTermText().toLowerCase()
											.startsWith("w")
									|| t.getTermText().toLowerCase()
											.startsWith("x")
									|| t.getTermText().toLowerCase()
											.startsWith("y")
									|| t.getTermText().toLowerCase()
											.startsWith("z")) {
								createTermIndexAndPlaceInProperMap(
										IndexWriter.u_z_termIndex, t, docId);
							} else {
								createTermIndexAndPlaceInProperMap(
										IndexWriter.symbol_termIndex, t, docId);
							}
						}
					}
				}

				// Retrieve tokens for NewsDate and add to TERM Index.. Need to
				// add a BOOST here
				TokenStream newsDateTokenStream = null;
				
				
				if (d.getField(FieldNames.NEWSDATE) != null)
					newsDateTokenStream = this.retrieveTokenStream(
							d.getField(FieldNames.NEWSDATE)[0],
							FieldNames.NEWSDATE);
				
				
				createIndexAndWriteDataTomap(IndexWriter.symbol_termIndex,
						newsDateTokenStream, docId);

				// Retrieve tokens for Author && Create Author Index
				TokenStream authorTokenStream = null;
				
				
				if (d.getField(FieldNames.AUTHOR) != null) {
					for (String s : d.getField(FieldNames.AUTHOR)) {
						authorTokenStream = this.retrieveTokenStream(s,
								FieldNames.AUTHOR);
						
						
						createIndexAndWriteDataTomap(IndexWriter.authorIndex,
								authorTokenStream, docId);
					}
				}

				// Retrieve tokens for Author Org and place them in Author Index
				TokenStream authOrgTokenStream = null;
				
				if (d.getField(FieldNames.AUTHORORG) != null)
					authOrgTokenStream = this.retrieveTokenStream(
							d.getField(FieldNames.AUTHORORG)[0],
							FieldNames.AUTHORORG);
			
				
				createIndexAndWriteDataTomap(IndexWriter.authorIndex,
						authOrgTokenStream, docId);

				
				// Retrieve tokens for Category && Create Category Index
				TokenStream categoryTokenStream = null;
				if (d.getField(FieldNames.CATEGORY) != null)
					categoryTokenStream = this.retrieveTokenStream(
							d.getField(FieldNames.CATEGORY)[0],
							FieldNames.CATEGORY);
				
				
				createIndexAndWriteDataTomap(IndexWriter.categoryIndex,
						categoryTokenStream, docId);

				
				
				
				// Retrieve tokens for Place && Create Place Index
				TokenStream placeTokenStream = null;
				if (d.getField(FieldNames.PLACE) != null)
					placeTokenStream = this.retrieveTokenStream(
							d.getField(FieldNames.PLACE)[0], FieldNames.PLACE);
				
				
				createIndexAndWriteDataTomap(IndexWriter.placeIndex,
						placeTokenStream, docId);
			}
		} catch (TokenizerException e) {
			System.out.println("Error while tokenizing the data");

		} catch (Exception e) {
			System.out.println("Error while tokenizing the data");
			/*
			 * throw new IndexerException(
			 * "Unexpected error occured during indexing.");
			 */
		}
	}

	private void createIndexAndWriteDataTomap(
			HashMap<String, ArrayList<IndexPostings>> index,
			TokenStream contentTokenStream, int docId) {
		ArrayList<IndexPostings> docsTokenIsIn = null;
		int termFrequencyCount = 0;
		if (contentTokenStream != null && contentTokenStream.getList() != null) {
			for (Token t : contentTokenStream.getList()) {
				if(t!=null && t.getTermText().trim().length()>0 && t.getTermText().toLowerCase().contains("adobe"))
					System.out.println(t.getTermText());
				if (index.get(t.getTermText()) != null) {
					docsTokenIsIn = index.get(t.getTermText());
					int flag = 0;
					int postingCount = 0;
					for (IndexPostings posting : docsTokenIsIn) {
						if (posting.getDocumentIdOfTerm() == docId) {
							termFrequencyCount = posting.getTermFrequency() + 1;
							posting.setTermFrequency(termFrequencyCount);
							docsTokenIsIn.set(postingCount, posting);
							flag = 1;
							break;
						}
						postingCount++;
					}
					if (flag == 0) {
						IndexPostings iP = new IndexPostings();
						iP.setDocumentIdOfTerm(docId);
						iP.setTermFrequency(1);
						docsTokenIsIn.add(iP);
					}
					index.put(t.getTermText(), docsTokenIsIn);
				} else {
					IndexPostings iP = new IndexPostings();
					iP.setDocumentIdOfTerm(docId);
					iP.setTermFrequency(1);
					docsTokenIsIn = new ArrayList<IndexPostings>();
					docsTokenIsIn.add(iP);
					index.put(t.getTermText(), docsTokenIsIn);
				}
			}
		}
	}

	/**
	 * Method that indicates that all open resources must be closed and cleaned
	 * and that the entire indexing operation has been completed.
	 * 
	 * @throws IndexerException
	 *             : In case any error occurs
	 */
	public void close() throws IndexerException {
		Thread t1 = new Thread(new IndexWriterRunnable(
				IndexWriter.docDictionary, indexDir, null));
		t1.start();
		Thread t2_1 = new Thread(new IndexWriterRunnable(
				IndexWriter.a_d_termIndex, indexDir + File.separator
						+ "term_a_d"));
		t2_1.start();
		Thread t2_2 = new Thread(new IndexWriterRunnable(
				IndexWriter.e_h_termIndex, indexDir + File.separator
						+ "term_e_h"));
		t2_2.start();
		Thread t2_3 = new Thread(new IndexWriterRunnable(
				IndexWriter.i_l_termIndex, indexDir + File.separator
						+ "term_i_l"));
		t2_3.start();
		Thread t2_4 = new Thread(new IndexWriterRunnable(
				IndexWriter.m_p_termIndex, indexDir + File.separator
						+ "term_m_p"));
		t2_4.start();
		Thread t2_5 = new Thread(new IndexWriterRunnable(
				IndexWriter.q_t_termIndex, indexDir + File.separator
						+ "term_q_t"));
		t2_5.start();
		Thread t2_6 = new Thread(new IndexWriterRunnable(
				IndexWriter.u_z_termIndex, indexDir + File.separator
						+ "term_u_z"));
		t2_6.start();
		Thread t2_7 = new Thread(new IndexWriterRunnable(
				IndexWriter.symbol_termIndex, indexDir + File.separator
						+ "term_symbol"));
		t2_7.start();
		Thread t3 = new Thread(new IndexWriterRunnable(IndexWriter.authorIndex,
				indexDir + File.separator + "author"));
		t3.start();
		Thread t4 = new Thread(new IndexWriterRunnable(
				IndexWriter.categoryIndex, indexDir + File.separator
						+ "category"));
		t4.start();
		Thread t5 = new Thread(new IndexWriterRunnable(IndexWriter.placeIndex,
				indexDir + File.separator + "place"));
		t5.start();
		try {
			t1.join();
			t2_1.join();
			t2_2.join();
			t2_3.join();
			t2_4.join();
			t2_5.join();
			t2_6.join();
			t2_7.join();
			t3.join();
			t4.join();
			t5.join();
		} catch (InterruptedException e) {
			throw new IndexerException(
					"InterruptedException while trying to join threads");
		} catch (Exception e) {
			throw new IndexerException("Unexpected error occurred");
		}
	}

	private TokenStream retrieveTokenStream(String contentAsString,
			FieldNames fieldName) throws TokenizerException {
		try {
			Tokenizer tokenizer = new Tokenizer();
			IndexWriter.tokenStream = tokenizer.consume(contentAsString
					.replaceAll("[\\.]", ""));
			FieldChainedAnalyzer fieldAnalyzer = (FieldChainedAnalyzer) AnalyzerFactory
					.getInstance().getAnalyzerForField(fieldName,
							IndexWriter.tokenStream);
			fieldAnalyzer.Filter();
			IndexWriter.tokenStream = fieldAnalyzer.getStream();
			// System.out.println("Begin SPECIALCHARS" + cal.getTime());
			time = time + IndexWriter.tokenStream.time;
			IndexWriter.tokenStream.reset();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			String message = "Error tokenizing data";
			throw new TokenizerException(message);
		}

		return tokenStream;
	}
}

/*
 * long t1 = Calendar.getInstance().getTimeInMillis(); long t2 =
 * Calendar.getInstance().getTimeInMillis();
 * System.out.println("Tokenizer & Analyser" + (t2 - t1) / 1000); long t3 =
 * Calendar.getInstance().getTimeInMillis(); System.out.println("Write to Map" +
 * (t3 - t2) / 1000); System.out.println(counter++); if (counter == 162 ||
 * counter == 163) { System.out.println(d.getField(FieldNames.CATEGORY)[0] + "/"
 * + d.getField(FieldNames.FILEID)[0]); }
 */