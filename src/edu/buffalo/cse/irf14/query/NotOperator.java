package edu.buffalo.cse.irf14.query;

import java.util.Map;

public class NotOperator implements Expression{
	String curExpression;
	String rightExpression; 

	public NotOperator(String cur, String right){
		if(cur!=null && cur.length()>0)
		  curExpression = cur;
		rightExpression = right;
	}
	
	public String interpret(){
		rightExpression = "<"+rightExpression+">";
		return "AND" + " " + rightExpression;
	}
}
