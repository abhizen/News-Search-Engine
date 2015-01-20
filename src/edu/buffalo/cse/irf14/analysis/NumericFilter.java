package edu.buffalo.cse.irf14.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumericFilter {
	private static Pattern quotesFilter = null;
	private static Pattern numericFilter = null;
	private Matcher mtch =null;
	private static NumericFilter instance = null;
	
	private NumericFilter()
	{
		
	}
	
	public static NumericFilter getInstance()
	{
		if(instance==null)
		{
			quotesFilter = Pattern.compile("\".*\"");
			numericFilter = Pattern.compile("[-+]?[0-9]*[\\.]?[\\,]?[\\-]?[\\/]?[0-9]+[\\%]?");
			instance = new NumericFilter();
		}
		return instance;
	}
	
	public String removeQuotes(String input)
	{
		mtch = quotesFilter.matcher(input);
		if(mtch.matches())
			input = input.replaceAll("[\"]","");
		
		return input;
	}
	
	public boolean checkNumber(String input)
	{
		input = removeQuotes(input);
		
		mtch = numericFilter.matcher(input);
		
		if(mtch.matches())
			return true;
		else
			return false;
	}
}
