package edu.buffalo.cse.irf14.query;

import java.util.EnumSet;
import java.util.Map;

import edu.buffalo.cse.irf14.document.FieldNames;

public class QueryTerm implements Expression{
String term;
String fieldName;

	public QueryTerm(String word, String fieldString)
	{
		if(word!=null && word.length()>0)
		{
			term = word;
		}
		else
			term = null;
		
		if(fieldString!=null && fieldString.length()>0)
			fieldName = fieldString;
		else
			fieldName = null;
	}
	
	public String interpret()
	{
		int index = 0;
		int tagFlag = 0;
		String tag = null;
		String keyWord = null;
		
		if(term==null && term.length()==0)
			return null;
		
		if(term.contains(":"))
		{
			index = term.indexOf(':');
			tag = term.substring(0, index);
			keyWord = term.substring(index, term.length());
			//FILEID, CATEGORY, TITLE, AUTHOR, AUTHORORG, PLACE, NEWSDATE, CONTENT
			
		    String fieldName[] = {"Fileid", "Category", "Title", "Author",
		    		"Authororg", "Place", "Newsdate", "Content"};
			
			for(String fNames : fieldName)
			{
				
				if(tag.toLowerCase().equals(fNames.toString().toLowerCase()))
				{
				   tag = fNames.toString();
				   tagFlag = 1; 
				   break;
				}
			}
			
			if(tagFlag == 0)
			{
				tag = null;
			}
				
		}
		
		if(tag == null)
		{
			return fieldName+":"+term;
		}
		else
		{
			return term;
		}
		
		
	}
}
