package edu.buffalo.cse.irf14.indexsearcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WordCounter {
	
	

	public Map<String, Integer> getWordCount(){

		String inputDirString = "training";
		File inputDirFile = new File(inputDirString);

		String[] subDirsStrings = inputDirFile.list();
		String[] subDirsFiles;
		File subDir;

		Map<String, Integer> wordCountPerFileMap = new HashMap<String, Integer>();
		try {

			for (String oneSubDir : subDirsStrings) {
				subDir = new File(inputDirString + File.separator + oneSubDir);
				subDirsFiles = subDir.list();
				BufferedReader bufferReader = null;

				if (subDirsFiles != null) {
					for (String file : subDirsFiles) {
						// Buffer read each file, and then list the file name as
						// key of a map and the count of words as the value of
						// the same

						String fileNameAbsolute = subDir.getAbsolutePath()
								+ File.separator + file;

						bufferReader = new BufferedReader(new FileReader(
								fileNameAbsolute));

						String line;
						StringBuilder contentBuilder = new StringBuilder();
						while ((line = bufferReader.readLine()) != null) {
							contentBuilder.append(line);
							contentBuilder.append(" ");
						}
						String content = contentBuilder.toString().trim();

						String[] splitStr = content.split("\\s+");
						wordCountPerFileMap.put(file, splitStr.length);
					}
					if (bufferReader != null) {
						bufferReader.close();
					}
				}
				
			}
			return wordCountPerFileMap;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return wordCountPerFileMap;
	}
}
