package edu.buffalo.cse.irf14.query;

public class Variable implements Expression{
	String word;
	
	public Variable(String term){
		if(term!=null && term.length()>0){
			word = term;
		}
		else
		  word = null;
	}
	
	
	public String interpret(){
		return word;
	}

}
