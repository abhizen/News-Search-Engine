package edu.buffalo.cse.irf14.query;

import java.util.Map;

public class BraceOperator implements Expression{
	String term;
	
	BraceOperator(String word){
		if(word!=null && word.length()>0)
		  term = word;
	}
	
	public String interpret(){
		String word = null;
		
		if(term.contains("("))
		   word = replaceBracket(term);
		
		
		if(term.contains(")"))
			word = replaceBracket(term);
		
		return word;
	}
	
	public String replaceBracket(String word){
		int index=0; 
		String tag = null;
		
		if(word.contains(":")){
			index = word.indexOf(":");
			tag = word.substring(0, index);
		}
		
		if(term.contains("(")){
			if(word.contains("<")){
				word =  word.replace("(", "");
				word =  word.replace("<", "[<");
			}
			else{
				word =  word.replace("(", "");
				word = "["+word; //word.replace(tag, "[Term");
			}
		}
		if(word.contains(")")){
			if(word.contains(">")){
				word =  word.replace(")", "");
				word =  word.replace(">", ">]");
			}
			else{
				word =  word.replace(")", "");
				word = word + "]";
			}
		}
		return word;
			
	}

}
