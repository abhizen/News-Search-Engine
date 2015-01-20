package edu.buffalo.cse.irf14.threads;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import edu.buffalo.cse.irf14.index.IndexPostings;

public class IndexWriterRunnable implements Runnable {

	private HashMap<String, ArrayList<IndexPostings>> indexData;
	private HashMap<Integer, String> docDictionary;
	private String indexDir;

	public IndexWriterRunnable(
			HashMap<String, ArrayList<IndexPostings>> indexData, String indexDir) {
		this.indexData = indexData;
		this.indexDir = indexDir;
	}

	public IndexWriterRunnable(HashMap<Integer, String> docDictionary,
			String indexDir, String temp) {
		this.docDictionary = docDictionary;
		this.indexDir = indexDir;
	}

	@Override
	public void run() {
		try {
			// 34 secs
			if (indexDir != null) {
				FileOutputStream fStream = null;
				ObjectOutputStream objStream = null;
				if (this.docDictionary != null) {
					fStream = new FileOutputStream(
							(new File(this.indexDir + File.separator
									+ "DocumentDictionary")).getAbsolutePath());
					objStream = new ObjectOutputStream(fStream);
					objStream.writeObject(this.docDictionary);
					objStream.close();
					fStream.close();
				}
				if (this.indexData != null) {
					fStream = new FileOutputStream(
							(new File(this.indexDir)).getAbsolutePath());
					objStream = new ObjectOutputStream(fStream);
					objStream.writeObject(this.indexData);
					objStream.close();
					fStream.close();
				}
			} else {
				throw new FileNotFoundException(
						"IndexWriter Error: The index directory that you have passed is not correct / available.");
			}
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out
					.println("IndexWriter Error: IO Exception in writing data to files");
		}

	}
}
