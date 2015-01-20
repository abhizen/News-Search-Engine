package edu.buffalo.cse.irf14.query;

public class BooleanOperator implements Expression{
	String firstString = null;
	String secondString = null;
	String defOpt = null;
	
	public BooleanOperator(String first,String second,String defOperator)
	{
		if(first!=null && first.length()>0)
			firstString = first;
		
		if(second!=null && second.length()>0)
			secondString = second;
		
		if(defOperator!=null && defOperator.length()>0)
			defOpt = defOperator;
		
	}
	
	public String interpret(){
		return firstString + " " + defOpt+" " + secondString;
	}
}
