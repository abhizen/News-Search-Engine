package edu.buffalo.cse.irf14.indexsearcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import edu.buffalo.cse.irf14.index.IndexPostings;
import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;
import edu.buffalo.cse.irf14.query.QueryParser;

public class QueryIndex {
	private String unparsedQuery;
	private String parsedQuery;
	private IndexReader termReader;
	private IndexReader categoryReader;
	private IndexReader titleReader;
	private IndexReader authorReader;
	private IndexReader authororgReader;
	private IndexReader placeReader;
	private IndexReader newsdateReader;
	private IndexReader contentReader;
	private Map<String, Integer> termList;
	private Map<String, Integer> oldTermList;
	private Map<String,Map<String, Integer>>  termPostList;
	private String oldOperator;
	private String defOperator = null;
	private int flag;
	

	public QueryIndex(String query, String opr) {
		if (query != null && query.length() > 0)
			unparsedQuery = query;

		if (opr != null && opr.length() > 0)
			defOperator = opr;

		parsedQuery = QueryParser.parse(query, defOperator).toString();

		termReader = new IndexReader(System.getProperty("INDEX.DIR"),
				IndexType.TERM);
		categoryReader = new IndexReader(System.getProperty("INDEX.DIR"),
				IndexType.CATEGORY);
		authorReader = new IndexReader(System.getProperty("INDEX.DIR"),
				IndexType.AUTHOR);
		placeReader = new IndexReader(System.getProperty("INDEX.DIR"),
				IndexType.PLACE);
		termPostList = new HashMap<String,Map<String, Integer>>();
		termList = null;
		oldTermList = null;
		flag = 0;
		oldOperator = null;
	}

	public Map<String, Integer> search() {

		Stack<String> term = new Stack<String>();
		Stack<String> operator = new Stack<String>();
		int flag = 0;
		if (!(parsedQuery.trim().contains(" ") && parsedQuery.trim().split(" ").length > 1)) {
			termList = singleList(parsedQuery);
			return termList;
		}
		

		for (String word : parsedQuery.split(" ")) {
			word = word.trim();
			if (!word.toLowerCase().equals("and")
					&& !word.toLowerCase().equals("or")) {

				if (term.contains("[") && termList != null) {
					oldTermList = termList;
					termList = null;
					oldOperator = operator.pop().toString();
				} else if (term.contains("]") && termList != null
						&& oldTermList != null) {
					flag = 1;
				}

				if (!operator.isEmpty() && !term.isEmpty()) {
					termList = mergeList(term.pop().toString(), word, operator
							.pop().toString());

					if (termList == null) {
						return null;
					}

					if (flag == 1 && termList != null && oldTermList != null) {
						termList = mergeList(null, null, oldOperator);
						flag = 0;
					}
				} else
					term.push(word);
			} else {
				operator.push(word);
			}
		}
		return termList;
	}

	public IndexReader getIndexReader(String word) {
		if (word.toLowerCase().contains("term"))
			return termReader;
		if (word.toLowerCase().contains("category"))
			return categoryReader;
		if (word.toLowerCase().contains("author"))
			return authorReader;
		if (word.toLowerCase().contains("place"))
			return placeReader;
		else
			return termReader;
	}

	public Map<String, Integer> mergeList(String first, String second,
			String operator) {
		String firstString = null;
		String secondString = null;
		String boolOpr = null;
		String firstStringTerm = null;
		String secStringTerm = null;
		IndexReader firstReader = null;
		IndexReader secondReader = null;
		Map<String, Integer> firstList = null;
		Map<String, Integer> secList = null;
		Iterator itf = null;
		Iterator its = null;
		int index = 0;

		if (first != null && first.length() > 0)
			firstString = first;
		else if (flag == 0)
			return null;

		if (second != null && second.length() > 0)
			secondString = second;
		else if (flag == 0)
			return null;

		if (operator != null && operator.length() > 0)
			boolOpr = operator;
		else if (flag == 0)
			return null;

		if (firstString.contains(":")) {
			index = firstString.indexOf(":");
			firstStringTerm = firstString
					.substring(index, firstString.length());
		}

		firstStringTerm = getTerm(firstString);
		secStringTerm = getTerm(secondString);

		firstReader = getIndexReader(firstStringTerm);
		secondReader = getIndexReader(secondString);

		if (termList == null)
		{
			firstList = firstReader.getPostings(firstStringTerm);
			termPostList.put(firstStringTerm, firstList);
		}
		else
			firstList = termList;

		if (oldTermList != null && flag == 1) {
			secList = oldTermList;
			flag = 0;
		} else
		{
			secList = secondReader.getPostings(secStringTerm);
			termPostList.put(secStringTerm,secList);
		}

		if (firstList != null && firstList.isEmpty() == false)
			itf = firstList.entrySet().iterator();

		if (secList != null && secList.isEmpty() == false)
			its = secList.entrySet().iterator();

		Map<String, Integer> finalList = new HashMap<String, Integer>();
		Map.Entry pairFirst = null;
		Map.Entry pairSec = null;

		if (operator.toLowerCase().contains("and")) {

			while (itf != null && its != null && itf.hasNext() && its.hasNext()) {

				if (pairFirst == null)
					pairFirst = (Map.Entry) itf.next();

				if (pairSec == null)
					pairSec = (Map.Entry) its.next();

				if (pairFirst.getKey() == pairSec.getKey()) {

					finalList.put(
							pairFirst.getKey().toString(),
							Integer.parseInt(pairFirst.getValue().toString())
									+ Integer.parseInt(pairSec.getValue()
											.toString()));

					pairFirst = (Map.Entry) itf.next();
					pairSec = (Map.Entry) its.next();
				} else if (Integer.parseInt(pairFirst.getKey().toString()) < Integer
						.parseInt(pairSec.getKey().toString())) {
					if (itf.hasNext())
						pairFirst = (Map.Entry) itf.next();
				} else {
					if (its.hasNext())
						pairSec = (Map.Entry) its.next();
				}
			}

		}
		if (operator.toLowerCase().contains("and")
				&& (second.contains("<") || second.contains(">"))) {

			while (itf != null && its != null && itf.hasNext() && its.hasNext()) {

				if (pairFirst == null)
					pairFirst = (Map.Entry) itf.next();

				if (pairSec == null)
					pairSec = (Map.Entry) its.next();

				if (pairFirst.getKey() == pairSec.getKey()) {

					pairFirst = (Map.Entry) itf.next();
					pairSec = (Map.Entry) its.next();
				} else if (Integer.parseInt(pairFirst.getKey().toString()) < Integer
						.parseInt(pairSec.getKey().toString()))
					pairFirst = (Map.Entry) itf.next();
				else
					pairSec = (Map.Entry) its.next();
			}

			while (itf != null && itf.hasNext()) {
				pairFirst = (Map.Entry) itf.next();
				finalList.put(pairFirst.getKey().toString(),
						Integer.parseInt(pairFirst.getValue().toString()));
			}

			while (its != null && its.hasNext()) {
				pairSec = (Map.Entry) its.next();
				finalList.put(pairSec.getKey().toString(),
						Integer.parseInt(pairSec.getValue().toString()));
			}

		}
		if (operator.toLowerCase().contains("or")) {

			while (itf != null && its != null && itf.hasNext() && its.hasNext()) {

				if (pairFirst == null)
					pairFirst = (Map.Entry) itf.next();

				if (pairSec == null)
					pairSec = (Map.Entry) its.next();

				if (pairFirst.getKey() == pairSec.getKey()) {

					finalList.put(
							pairFirst.getKey().toString(),
							Integer.parseInt(pairFirst.getValue().toString())
									+ Integer.parseInt(pairSec.getValue()
											.toString()));

					pairFirst = (Map.Entry) itf.next();
					pairSec = (Map.Entry) its.next();
				} else if (Integer.parseInt(pairFirst.getKey().toString()) < Integer
						.parseInt(pairSec.getKey().toString())) {
					finalList.put(pairFirst.getKey().toString(),
							Integer.parseInt(pairFirst.getValue().toString()));
					pairFirst = (Map.Entry) itf.next();
				} else {
					finalList.put(pairSec.getKey().toString(),
							Integer.parseInt(pairSec.getValue().toString()));
					pairSec = (Map.Entry) its.next();
				}
			}

			while (itf != null && itf.hasNext()) {
				pairFirst = (Map.Entry) itf.next();
				finalList.put(pairFirst.getKey().toString(),
						Integer.parseInt(pairFirst.getValue().toString()));
			}

			while (its != null && its.hasNext()) {
				pairSec = (Map.Entry) its.next();
				finalList.put(pairSec.getKey().toString(),
						Integer.parseInt(pairSec.getValue().toString()));
			}

		}
		return finalList;
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

	public Map<String, Integer> singleList(String query) {
		String term = null;
		IndexReader termReader = null;
		Map<String, Integer> postList = null;

		if (query == null || query.length() <= 0)
			return null;

		term = getTerm(query);

		termReader = getIndexReader(term);
		postList = termReader.getPostings(term);
		
		if(postList!=null)
			termPostList.put(term, postList);
		
		return postList;
	}

	public TreeMap<String, ArrayList<IndexPostings>> getIndex() {
		return termReader.getIndex();
	}
	
	public Map<String,Map<String, Integer>> getTermPostList()
	{
		return this.termPostList;
	}
}
