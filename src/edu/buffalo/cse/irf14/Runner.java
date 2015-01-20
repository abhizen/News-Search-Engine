/**
 * 
 */
package edu.buffalo.cse.irf14;

import java.io.File;

import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.Parser;
import edu.buffalo.cse.irf14.document.ParserException;
import edu.buffalo.cse.irf14.index.IndexWriter;
import edu.buffalo.cse.irf14.index.IndexerException;

/**
 * @author nikhillo
 * 
 */
public class Runner {
	/**
	 * 
	 */
	public Runner() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String ipDir = args[0];
		String indexDir = args[1];
		// more? idk!
		long startTime = 0;
		long endTime = 0; 
		long totaltime =0;
		File ipDirectory = new File(ipDir);
		String[] catDirectories = ipDirectory.list();

		String[] files;
		File dir;

		Document d = null;
		IndexWriter writer = new IndexWriter(indexDir);
		startTime = System.nanoTime();
		try {
			for (String cat : catDirectories) {
				dir = new File(ipDir + File.separator + cat);
				files = dir.list();

				if (files == null)
					continue;

				for (String f : files) {
					try {
						
						d = Parser.parse(dir.getAbsolutePath() + File.separator
								+ f);
						
						writer.addDocument(d);
					} catch (ParserException e) {
						e.printStackTrace();
					}

				}

			}
			System.out.println("Time taken by module"+" "+writer.time);
			writer.close();
			endTime = System.nanoTime();
			totaltime = totaltime + (endTime - startTime);
			System.out.println("Took "+totaltime + " ns");
		
		} catch (IndexerException e) {
			e.printStackTrace();
		}
	}

}
