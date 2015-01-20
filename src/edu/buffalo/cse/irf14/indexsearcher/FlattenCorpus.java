package edu.buffalo.cse.irf14.indexsearcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FlattenCorpus {

	public static void main(String args[]) {

		InputStream inStream = null;
		OutputStream outStream = null;

		String inputDirString = "training";
		String corpusDir = "korpus_dir";
		try {
			File inputDirFile = new File(inputDirString);
			// if prev corpus dir exists, just delete it.. so no old (count)
			// values
			// are present
			File dir = new File(inputDirFile.getAbsolutePath() + File.separator
					+ corpusDir);
			if (dir.isDirectory()) {
				File[] files = dir.listFiles();
				for (File subFile : files)
					subFile.delete();
				dir.delete();
			}
			// create corpus dir
			new File(inputDirFile.getAbsolutePath() + File.separator
					+ corpusDir).mkdirs();

			String[] subDirsStrings = inputDirFile.list();
			String[] subDirsFiles;
			File subDir;

			for (String oneSubDir : subDirsStrings) { // oneSubDir is the parent
														// sub
														// dirs
				if (!oneSubDir.equals("korpus_dir")) {
					subDir = new File(inputDirString + File.separator
							+ oneSubDir);
					subDirsFiles = subDir.list();
					if (subDirsFiles != null) {
						for (String file : subDirsFiles) { // file is the file
															// name
															// in
															// sub dirs
							System.out.println(file);
							String sourceFileString = subDir.getAbsolutePath()
									+ File.separator + file;
							File sourceFile = new File(sourceFileString);

							String destFileString = inputDirFile
									.getAbsolutePath()
									+ File.separator
									+ corpusDir + File.separator + file;
							File destFile = new File(destFileString);
							int count = 1;
							while (destFile != null && destFile.isFile()) {
								destFile = new File(destFileString + "("
										+ count + ")");
								count++;
							}
							inStream = new FileInputStream(sourceFile);
							outStream = new FileOutputStream(destFile);

							byte[] buffer = new byte[1024];

							int fileLen;
							while ((fileLen = inStream.read(buffer)) > 0)
								outStream.write(buffer, 0, fileLen);

							inStream.close();
							outStream.close();

						}
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}