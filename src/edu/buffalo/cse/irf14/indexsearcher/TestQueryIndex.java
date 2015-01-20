package edu.buffalo.cse.irf14.indexsearcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.Map.Entry;

import edu.buffalo.cse.irf14.SearchRunner;

public class TestQueryIndex {

	public static void main(String[] args) {
		//String term = "controlling interest";
		String term = "adobe ";
		Map.Entry pairFirst = null;

		/*
		 * QueryIndex obj = new QueryIndex("{controlling interest}","AND");
		 * //System.out.println(obj.\\)
		 * 
		 * @SuppressWarnings("unused") Map<String,Integer> termlist =
		 * obj.search(); if(termlist==null || termlist.isEmpty()){
		 * System.out.println("Not found"); } else{ Iterator itf =
		 * termlist.entrySet().iterator();
		 * 
		 * while(itf.hasNext()){ pairFirst = (Map.Entry)itf.next();
		 * System.out.println(pairFirst.getKey()); } }
		 */

		/*
		 * Map<String,Double> scoreMap=null; Map.Entry<String, Double> entry=
		 * null;
		 * 
		 * 
		 * OkapiModel obj = new OkapiModel("controlling interest","OR");
		 * scoreMap = obj.getTotalScore();
		 * 
		 * if(scoreMap!=null && !scoreMap.isEmpty()){ Map<String,Double> map =
		 * new TreeMap<String,Double>(scoreMap); Iterator<Map.Entry<String,
		 * Double>> itr = map.entrySet().iterator(); while(itr.hasNext()){ entry
		 * = itr.next(); //if(entry.getValue()>0)
		 * 
		 * System.out.println("Doc:Id:"+entry.getKey()+" "+"Score:"+entry.getValue
		 * ()); } }
		 */

		// Scanner objscanner = new Scanner(System.in);
		/*
		 * PrintStream stream = new PrintStream(System.out);
		 * stream.println("Abhinit");
		 */
		// stream.append("Sinha", 0, 5);

		// stream.close();

		File fr = null;
		fr = new File("query.txt");
		System.out.println("query.txt");

		File output = new File("results.txt");

		PrintStream stream = null;
		try {
			FileOutputStream os = new FileOutputStream(
					"D:\\Presentation_Tier\\newsindexer_II_final\\results.txt",
					true);
			stream = new PrintStream(os);

		} catch (FileNotFoundException ex) {

		}
		SearchRunner objSearchRunner = new SearchRunner("IndexDirectory",
				"corpus", 'Q', stream);
		objSearchRunner.query(fr);
		
		/*
		 * String temp = getSnnippets("0004301"); System.out.println(temp);
		 * 
		 * Map<String,Double> scoreMap= new LinkedHashMap();//new
		 * HashMap<String,Double>(); scoreMap.put("1010", 1.0);
		 * scoreMap.put("1020", 8.0); scoreMap.put("1030", 3.0);
		 * scoreMap.put("1040", 4.0); scoreMap.put("1050", 5.0);
		 * 
		 * 
		 * Map<String, Double> reverseSortedScoreList = sortByValues(scoreMap);
		 * 
		 * Iterator<Map.Entry<String, Double>> itr =
		 * reverseSortedScoreList.entrySet().iterator(); Map.Entry<String,
		 * Double> entry= null;
		 * 
		 * int index=0; while(itr.hasNext() && index<10){ entry = itr.next();
		 * if(entry.getValue()>0.0){
		 * //System.out.println("Doc:Id:"+entry.getKey(
		 * )+" "+"Score:"+entry.getValue());
		 * System.out.println(entry.getKey()+"#"+entry.getValue()+"\n");
		 * index++; } }
		 */
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Double> sortByValues(Map<String, Double> map) {
		List<Map.Entry<String, Double>> list = new LinkedList(map.entrySet());

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
	public static String getTerm(String word) {
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


	private static String getSnnippets(String docId) {
		File inputFiles = new File("corpus");
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
						if (line.length() > 0 && !line.equals(" "))
							contentBuilder.append(line);
						contentBuilder.append(" ");
						if (index > 2)
							break;
						index++;
					}
					line = contentBuilder.toString();
					break;
				}
			}
		} catch (Exception e) {
			System.exit(1);
		}

		return line;
	}
}
