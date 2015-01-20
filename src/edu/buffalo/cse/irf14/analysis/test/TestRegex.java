/**
 * 
 */
package edu.buffalo.cse.irf14.analysis.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import snowballstemmer.PorterStemmer;
/**
 * @author Dell
 *
 */
public class TestRegex {

	/**
	 * @param args
	 */
	
	/*public  String getString(String... temp)
	{
		String text="";
		for(int i=0;i<temp.length;i++)
		{
			text = text +" " + temp[i];
		}
		return text;
	}*/
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String str="(([\\+\\<\\>\\'\\/\\,\\\"\\)\\(\\&\\#\\;]*[^\\+\\<\\>\\'\\/\\,\\\"\\)\\(\\&\\#\\;]*)*)";
		
		Pattern ptr;
		Matcher match;
		String input="#3;";
		System.out.println(input);
		ptr = Pattern.compile(str);
		match =ptr.matcher(input);
		if(match.matches()==true)
		{
			System.out.println("Its a number\0");
			input=input.replaceAll("#","");
			input=input.replaceAll("\\;","");
			
			System.out.println(input);
		}
		
		
		/*PorterStemmer stemmer = new PorterStemmer();
        
		//set the word to be stemmed
		stemmer.setCurrent("thankful");
		             
		//call stem() method. stem() method returns boolean value. 
		if(stemmer.stem())
		{
		    //If stemming is successful obtain the stem of the given word
		    System.out.println(stemmer.getCurrent());
		}
		
		char ch='a';
		String unicodeChar = String.format("\\u%04x", (int) ch);
		System.out.println(unicodeChar);
		*/
		input="1948,";
		//ptr = Pattern.compile("([\\s]*[1-3][0-9]?[\\,]?[\\s]*$)");
		ptr =Pattern.compile("([\\s]*[1][89][0-9][0-9]?[\\,]?[\\s]*$)");
		if(ptr.matcher(input).matches())
		{
			System.out.println("working");
		}
		
		input="2012-13";
		ptr= Pattern.compile("([\\s]*[12][089][0-9][0-9]([\\-][0-9]?[0-9])?[\\.]?[\\,]?[\\s]*)");
		
		if(ptr.matcher(input).matches())
		{
			System.out.println("matched");
		}
		
		input="7:15PM";
		String tempString[] = input.split("[\\:]");
		
    	System.out.println(tempString[0]);
    	System.out.println(tempString[1]);
		
    	input="2012-13.";
    	input = input.replaceAll("[\\.]","");
    	String str1[] = input.split("[\\-]");
    	System.out.println(str1[0]);
    	System.out.println(str1[1]);
		int thisYear = Integer.parseInt(str1[0].substring(str1[0].length()-3,str1[0].length()));
		System.out.println(thisYear);
		int NextYear=Integer.parseInt(str1[1]);
		NextYear=Integer.parseInt(str1[0]) + NextYear-thisYear;
		String mth="01";
		String day="01";
		input=str1[0]+mth+day+"-"+Integer.toString(NextYear)+mth+day;
		System.out.println(str1[0]);
    	System.out.println(input);
    	/*System.out.println(str1[1]);*/
		//matcher = ptr.matcher(input);
		/*Pattern for year number detection*/
		//ptr1 = Pattern.compile("([\\s]*[0-9]{1,4}[\\,]?[\\s]*$)");
		//ptr1=Pattern.compile("([\\s]*[1][89][0-9][0-9]?[\\,]?[\\s]*$)");
    	
    	ptr=Pattern.compile("([^\\?]*[\\?]+$)");
    	input="123??!!??";
    	
    	input="I'm";
    	ptr = Pattern.compile("((.*[\\'ve]$)|(.*[\\'m]$)|(.*[\\'re]$)|(.*[n\\'t]$)|(.*[I\\'m]$)|(.*[\\'re]$)|(.*[\\'d]$)|(.*[\\'ll]$)|(.*[\\'em]$))");
		
		if(ptr.matcher(input).matches())
		{
			System.out.println("matching");
		}
		
		
	}

}
