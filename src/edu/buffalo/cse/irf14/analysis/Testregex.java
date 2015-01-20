package edu.buffalo.cse.irf14.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Testregex {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long startTime = System.nanoTime();
		Pattern numericPattern = Pattern
				.compile("(([\\\"]*[\\s]*[\0]*[\\.]*[0-9]*[\\,]?[\\-]*[\\/]?[\\s]*[0-9]*[\0]*[\\\"]*)*"
						+ "([\\\"]*[\0]*[\\.]*[0-9]*[\\,]?[\\-]*[\\/]?[\\s]*[0-9]*[\0]*[\\s]*[\\\"]*)$)|([\\\"]*[\0]*[\\.]*[0-9]*[\\.]?[\\s]*[0-9]*[\\%]?[\\.]*[\0]*[\\\"]*)");
		String input = "+4895,321";
		Matcher mtch = numericPattern.matcher(input);
		if(mtch.matches())
			System.out.println("matched");
		
		long endTime = System.nanoTime();
		System.out.println("Took "+(endTime - startTime) + " ns");
		
		startTime = System.nanoTime();
		numericPattern = Pattern.compile("([\\\"]*[\\s]*[\0]*[\\.]*[0-9]*[\\,]?[\\-]*[\\/]?[\\s]*[0-9]*[\0]*[\\\"]*)+");
		mtch = numericPattern.matcher(input);
		if(mtch.matches())
			System.out.println("matched");
		endTime = System.nanoTime();
		System.out.println("Took "+(endTime - startTime) + " ns");
		
		startTime = System.nanoTime();
		numericPattern = Pattern.compile("[-+]?[0-9]*[\\.]?[\\,]?[\\-]?[\\/]?[0-9]+");
		mtch = numericPattern.matcher(input);
		if(mtch.matches())
			System.out.println("matched");
		endTime = System.nanoTime();
		System.out.println("Took "+(endTime - startTime) + " ns");
		input="\"abc\"";
		System.out.println(input);
		numericPattern = Pattern.compile("(^\".*\"$)");
		mtch = numericPattern.matcher(input);
		if(mtch.matches())
		{
			input = input.replaceAll("[\"]","");		
			System.out.println("matched");
			System.out.println(input);
		}
		
		input = "775000";
		numericPattern = Pattern.compile("[-+]?[0-9]*[\\.]?[\\,]?[\\-]?[\\/]?[0-9]+[\\%]?");
	    mtch = numericPattern.matcher(input);
		if(mtch.matches())
		{
			System.out.println("number matched");
		}
		
		NumericFilter obj = NumericFilter.getInstance();
		
		if(obj.checkNumber(input))
		{
			System.out.println("matched");
		}
		
		input = "99BC,";
		Pattern christYearPattern1 = Pattern.compile("[0-9]*((AD)|(BC)){1}[\\,]?[\\.]?");
		mtch = christYearPattern1.matcher(input);
		if(mtch.matches())
		{
			System.out.println("year matched");
		}
		
		System.out.println("BC"+input.indexOf("99"));
	}

}
