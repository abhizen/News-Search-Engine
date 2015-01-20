package edu.buffalo.cse.irf14.analysis;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.*;

public class DateFilter {
	
	Map<String,String> mthMap;
	Pattern yearPattern = null;
	Pattern dayPattern = null;
	Pattern christYearPattern1 = null;
	Pattern christYearPattern2 = null;
	Pattern adbcYearPattern = null;
	Pattern timePattern1 = null;
	Pattern timePattern2 = null;
	Pattern timePattern3 = null;
	String day = null;
	String year = null;
	String month = null;
	int prevIndex = -1;
	int nextIndex = -1;
	int nextNextIndex = -1;
	
	public  DateFilter()
	{
		mthMap = new HashMap<String,String>();
	    
		mthMap.put("january","01");
		mthMap.put("february","02");
		mthMap.put("march","03");
		mthMap.put("april","04");
		mthMap.put( "may","05");
		mthMap.put("june","06");
		mthMap.put("july","07");
		mthMap.put("august","08");
		mthMap.put("september","09");
		mthMap.put("october","10");
		mthMap.put("november","11");
		mthMap.put("december","12");		
		mthMap.put("jan","01");
		mthMap.put("feb","02");
		mthMap.put("mar","03");
		mthMap.put("apr","04");
		mthMap.put("jun","06");
		mthMap.put("jul","07");
		mthMap.put("aug","08");
		mthMap.put("sep","09");
		mthMap.put("oct","10");
		mthMap.put("nov","11");
		mthMap.put("dec","12");
		
		yearPattern = Pattern.compile("(([1][89][0-9][0-9])|([2][0-1][0-9][0-9]))[\\,]?[\\.]?");
		dayPattern = Pattern.compile("([0-3]?[0-9])");
		christYearPattern1 = Pattern.compile("[0-9]*((AD)|(BC)){1}[\\,]?[\\.]?");
		christYearPattern2 = Pattern.compile("((AD)|(BC))[\\,]?[\\.]?");
		adbcYearPattern = Pattern.compile("[0-9][0-9]");
		timePattern1 = Pattern.compile("([0-2]?[0-9][\\:][0-9]?[0-9])?((am)|(pm)|(AM)|(PM)){1}[\\,]?[\\.]?");
		timePattern2 = Pattern.compile("((am)|(pm)|(AM)|(PM)){1}[\\,]?[\\.]?");
		timePattern3 = Pattern.compile("([0-2]?[0-9][\\:][0-9]?[0-9])");
	}
	
	public String checkMonth(String mth)
	{
		String inputMonth =null;
		if(mth!=null && mth.length()>0)
			inputMonth = mth.trim().toLowerCase();
		else
			return null;
		
		if(inputMonth.contains(","))
			inputMonth = inputMonth.replaceAll("[\\,]", "");
		
		if(inputMonth.contains("."))
			inputMonth = inputMonth.replaceAll("[\\.]", "");
		
	    if(mthMap.containsKey(inputMonth))
	    {
	    	
			return inputMonth;
	    }
	    else
	    	return null;
	}
	
	public boolean processMonth(TokenStream tempTokenStream,String mth)
	{
		int status =0;
		String prevString =null;
		String nextString =null;
		String nextNextString =null;
		String formatYear = null;
		String inputMonth = null;
		
		if((inputMonth=checkMonth(mth))==null)
			return false;
			
		status = tempTokenStream.getStatus();
		
		if(status>0)
			prevString = tempTokenStream.getToken(status-1).getTermText();
		
		if(tempTokenStream.hasNext())
			nextString = tempTokenStream.getToken(status+1).getTermText();
	
		if(tempTokenStream.getToken(status+2)!=null)
			nextNextString = tempTokenStream.getToken(status+2).getTermText();
		
		extractDayYear(prevString,nextString,nextNextString,tempTokenStream);
		
		month = mthMap.get(inputMonth);
		
		formatYear = year+month+day;
		
		if(formatYear.contains("."))
		{
			formatYear = formatYear.replaceAll("[\\.]", "");
			formatYear = formatYear+".";
		}
		
		if(formatYear.contains(","))
		{
			formatYear = formatYear.replaceAll("[\\,]", "");
			formatYear = formatYear+",";
		}
		
		Token tempToken = new Token();
		tempToken.setTermText(formatYear);
		tempTokenStream.setValue(tempToken,tempTokenStream.getStatus());
		
		if(nextNextIndex>-1)
			tempTokenStream.removeToken(nextNextIndex);
		
		if(nextIndex>-1)
			tempTokenStream.removeToken(nextIndex);
		
		if(prevIndex>-1)
			tempTokenStream.removeToken(prevIndex);
		
		
		return true;
	}
	
	public boolean processYear(TokenStream tempTokenStream,String inputYear)
	{
		int status =0;
		String formatYear = null;
		
		if(checkYear(inputYear)==null)
			return false;
		
		status = tempTokenStream.getStatus();
		
		if(tempTokenStream.getToken(status+1)!=null)
		{
		if(checkMonth(tempTokenStream.getToken(status+1).getTermText())!=null)
			return false;
		}
		else
			return false;
		
		formatYear = inputYear+"01"+"01";
		
		Token tempToken = new Token();
		tempToken.setTermText(formatYear);
		tempTokenStream.setValue(tempToken,tempTokenStream.getStatus());
		
		return true;
	}
	
	public boolean processChristYear(TokenStream tempTokenStream,String inputYear)
	{
		int status =0;
		String formatYear = null;
		Matcher mtch = null;
		Matcher mtch1 = null;
		
		if(checkChristYear(inputYear)==null)
			return false;
		
		
		mtch = christYearPattern2.matcher(inputYear);
		 
		if(mtch.matches())
		{
			status = tempTokenStream.getStatus();
			if(status>0)
			{
				mtch1 = adbcYearPattern.matcher(tempTokenStream.getToken(status-1).getTermText());
				if(mtch1.matches())
				{
					this.year = padZero(tempTokenStream.getToken(status-1).getTermText())+"01"+"01";
					if(inputYear.contains("BC"))
						this.year = "-"+ this.year;
				}
				else
				  return false;
			}
		}	
		else
		{
			if(inputYear.contains("BC"))
			{
				this.year = inputYear.replace("BC","");
				this.year = "-"+padZero(this.year)+"01"+"01";
			}
			else if(inputYear.contains("AD"))
			{
				this.year = inputYear.replace("AD","");
				this.year = padZero(this.year)+"01"+"01";
			}
		}
		
		if(inputYear.contains("."))
		{
			if(this.year.contains("."))
				this.year = this.year.replaceAll("[\\.]", "");
			
			this.year = this.year + ".";
		}
		else if(inputYear.contains(","))
		{
			if(this.year.contains(","))
				this.year = this.year.replaceAll("[\\,]", "");
			
			this.year = this.year + ",";
		}
		
		Token tempToken = new Token();
		tempToken.setTermText(this.year);
		tempTokenStream.setValue(tempToken,tempTokenStream.getStatus());	
		
		if(mtch1!=null && mtch1.matches())
			tempTokenStream.removeToken(status-1);
		
		return true;
	}
	public boolean processTime(TokenStream tempTokenStream,String time)
	{
		int status =0;
		String formatTime = null;
		Matcher mtch = null;
		Matcher mtch1 = null;
		
		if(checkTime(time)==null)
			return false;
		
		status = tempTokenStream.getStatus();
		mtch = timePattern2.matcher(time);
		if(mtch.matches())
		{
			if(status>0)
				mtch1 = timePattern3.matcher(tempTokenStream.getToken(status-1).getTermText());
			else
				return false;
			
			if(mtch1.matches())
			{
				int index = 0;
				
				formatTime = tempTokenStream.getToken(status-1).getTermText();
				if(time.toLowerCase().contains("pm"))
				{
					index = formatTime.indexOf(":"); 
					formatTime = convertToStandardTime(formatTime.substring(0,index),formatTime.substring(index+2,formatTime.length()));
				}
				
				formatTime = formatTime+":00";
			}
			else
				return false;
		}
		else
		{
			int index = 0;
			int strLength = time.length();
			String tempTime = time.toLowerCase();
			
			if(tempTime.contains("am"))
			{
				index= tempTime.indexOf("am");
				formatTime = tempTime.substring(0, index)+":00";
			}
			else if(tempTime.contains("pm"))
			{
				int hour =0;
				index = tempTime.indexOf(":");
				
				formatTime = tempTime.replace("pm","");
				strLength =  formatTime.length();
				formatTime = convertToStandardTime(tempTime.substring(0, index),formatTime.substring(index+1, strLength));
				
				formatTime = formatTime+":00";
			}	
			
		}
		
		if(time.contains(","))
		{
			if(formatTime.contains(","))
				formatTime = formatTime.replaceAll("[\\,]", "");
		
			formatTime = formatTime + ",";
		}
		else if(time.contains("."))
		{
			if(formatTime.contains("."))
				formatTime = formatTime.replaceAll("[\\.]", "");
			
			formatTime = formatTime + ".";
		}
			
		Token tempToken = new Token();
		tempToken.setTermText(formatTime);
		tempTokenStream.setValue(tempToken,tempTokenStream.getStatus());	
		
		if(mtch1!=null && mtch1.matches())
			tempTokenStream.removeToken(status-1);
		
		return true;
		
	}
	
	private String convertToStandardTime(String strHour,String strMin)
	{
		int hour = Integer.parseInt(strHour);
		
		String formatTime = null;
		
		if(hour<12)
			hour = hour + 12;
		
		formatTime = Integer.toString(hour)+":"+strMin;
		
		return formatTime;
	}
	public String padZero(String input)
	{
		String tempInput =null;
		int strLength = 0;
	
		
		if(input!=null)
		{
			tempInput = input;
			
			if(tempInput.contains("."))
				tempInput = tempInput.replaceAll("[\\.]", "");
			else if(tempInput.contains(","))
				tempInput = tempInput.replaceAll("[\\,]", "");
			
			strLength = tempInput.length();
			if(strLength<4)
			{
				for(int i=0;i<(4-strLength);i++)
					tempInput = "0" + tempInput;
			}
		}
		else
			return null;
		
		return tempInput;
	}
	public void process(TokenStream tempTokenStream,String mth)
	{
		if(processMonth(tempTokenStream,mth))
		  return;
		else if(processYear(tempTokenStream,mth))
		  return;	
		else if(processChristYear(tempTokenStream,mth))
		  return;
		else if(processTime(tempTokenStream,mth))
		  return;
	}
	/*This function checks if parameter is a year*/
	public String checkYear(String year)
	{
		String inputYear =null;
		Matcher mtch = null;
		
		if(year!=null && year.length()>0)
			inputYear = year;
		else
			return null;
		
		mtch = yearPattern.matcher(inputYear);
		
		if(mtch.matches())
			return inputYear;
		else
			return null;
	}
	
	public String checkChristYear(String year)
	{
		String inputYear =null;
		Matcher mtch = null;
		
		if(year!=null && year.length()>0)
			inputYear = year;
		else
			return null;
		
		mtch = christYearPattern1.matcher(inputYear);
		
		if(mtch.matches())
			return inputYear;
		else
			return null;
	}
	
	public String checkDay(String day)
	{
		String inputDay = null;
		Matcher mtch = null;
		
		if(day!=null && day.length()>0)
			inputDay = day;
		else
			return null;
		
		if(inputDay.contains(","))
			inputDay = inputDay.replaceAll("[\\,]", "");
			
	    if(inputDay.contains("."))
	    	inputDay = inputDay.replaceAll("[\\.]", "");
		
	    if(inputDay.length()==1)
	    	inputDay = "0"+inputDay;
	    
	    mtch = dayPattern.matcher(inputDay);
	    
	    if(mtch.matches())
	    	return inputDay;
	    else
	    	return null;
	}
	
	public String checkTime(String time)
	{
		String inputTime =null;
		Matcher mtch = null;
		
		if(time!=null && time.length()>0)
			inputTime = time;
		else
			return null;
		
		mtch = timePattern1.matcher(time);
		
		if(mtch.matches())
			return time;
		else
			return null;
		
		
	}
	public void extractDayYear(String first,String sec,TokenStream tempTokenStream)
	{
		int status = tempTokenStream.getStatus();
		
		
		if(first!=null && first.length()>0)
		{
			if((year=checkYear(first))!=null)
			{	
				prevIndex = status-1;
			}
			else if((day=checkDay(first))!=null)
			{
				prevIndex = status-1;
			}
		}
		
		if(sec!=null && sec.length()>0)
		{
			if((year=checkYear(sec))!=null)
			{
				nextIndex = status+1;
			}
			else if((day=checkDay(sec))!=null)
			{		
				nextIndex = status+1;
			}
		}
		
		if(year==null)
			year = "1900";
			
		
		if(day==null)
			day = "01";
	}
	
	public void extractDayYear(String first,String sec,String third,TokenStream tempTokenStream)
	{
		int status = tempTokenStream.getStatus();
		String tempYear = null;
		String tempDay = null;
		
		
		if(first!=null && first.length()>0)
		{
			if((tempYear=checkYear(first))!=null)
			{	
				year = tempYear;
				prevIndex = status-1;
			}
			else if((tempDay=checkDay(first))!=null)
			{
				day = tempDay; 
				prevIndex = status-1;
			}
		}
		
		if(sec!=null && sec.length()>0)
		{
			if((tempYear=checkYear(sec))!=null)
			{
				year = tempYear;
				nextIndex = status+1;
			}
			else if((tempDay=checkDay(sec))!=null)
			{		
				day = tempDay;
				nextIndex = status+1;
			}
		}
		
		if(third!=null && third.length()>0)
		{
			if((tempYear=checkYear(third))!=null)
			{
				year = tempYear;
				nextNextIndex = status+2;
			}
			else if((tempDay=checkDay(third))!=null)
			{		
				day = tempDay;
				nextNextIndex = status+2;
			}
		}
		
		if(year==null)
			year = "1900";
			
		
		if(day==null)
			day = "01";
		
	}

}
