/**
 * 
 */
package edu.buffalo.cse.irf14.query;

import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

/**
 * @author Dell
 *
 */
public class Evaluator implements Expression{
	private Expression syntaxTree;
	String field ;
	
	public Evaluator(String expression, String defOperator) {
		
		
        Stack<Expression> expressionStack = new Stack<Expression>();
        Stack<Expression> tempExpressionStack = new Stack<Expression>();
        Expression subExpression = null;
        String tag = null;
        field = "Term";
        
        if(expression!=null && expression.length()>0){
        	
        	for(String word : expression.split(" ")){
        		word = word.trim();
        		
        		if(!word.toLowerCase().equals("and") && !word.toLowerCase().equals("or") && !word.toLowerCase().equals("not")){
        			tag = CheckField(word);
        			subExpression = new QueryTerm(word.toString(),tag);
        			subExpression = new Variable(subExpression.interpret());
        			expressionStack.push(subExpression);
        		}
        		else
        		{
        			subExpression = new Variable(word);
        			
        			expressionStack.push(subExpression);
        		}
        		
        	}
        }
       
        while(!expressionStack.isEmpty())
        {
        	tempExpressionStack.push(expressionStack.pop());
        }
        
        expressionStack = tempExpressionStack;
        Stack<Expression> expressionStackNew = new Stack<Expression>();
        
        if(expression!=null && expression.length()>0){
        	
          	int flag = 0;
        	 
        	while(!expressionStack.isEmpty())	{
        		subExpression = expressionStack.pop();
        		String word = subExpression.interpret();
        		word = word.trim();
         		
        		if(word.toLowerCase().equals("not") || (word.contains("(") || word.contains(")")))
        		{
        			if(word.toLowerCase().equals("not")){
        				if(expressionStack.isEmpty())
        				{
        					flag=1;
        				}
				
        				subExpression = new NotOperator(word,expressionStack.pop().interpret());
        				word = subExpression.interpret();
        				
        			}
        			if(word.contains("(") || word.contains(")")){
        				if(expressionStack.isEmpty())
        				{
        					flag=1;
        				}
        				subExpression = new BraceOperator(word);
        				word = subExpression.interpret();
        				    			
        			}
        			
        			subExpression = new Variable(word);
    				expressionStackNew.push(subExpression);
        		}
        
        		else{
        			if(expressionStack.isEmpty())
        			{
        				flag=1;
        			}
        			subExpression = new Variable(word);
        			expressionStackNew.push(subExpression);
        		}
        		 
			  	if(flag == 1)
			      break;
			   
        	}
        }
        
        expressionStack = expressionStackNew;
        while(!expressionStack.isEmpty())
        {
        	tempExpressionStack.push(expressionStack.pop());
        }
        
        expressionStack = tempExpressionStack;
        expressionStackNew = new Stack<Expression>();;
        
        while(!expressionStack.isEmpty())	{
        	subExpression = expressionStack.pop();
        	String firstString = subExpression.interpret();
        	String secondString = null;
        	if(!expressionStack.isEmpty() && !firstString.toLowerCase().contains("and") && !firstString.toLowerCase().contains("or") && !firstString.toLowerCase().contains("not")){
        		secondString = expressionStack.pop().interpret();
        		
        		if(!secondString.toLowerCase().contains("and") && !secondString.toLowerCase().contains("or") && !secondString.toLowerCase().contains("not")){
        			subExpression =new  BooleanOperator(firstString,secondString,defOperator);
        			firstString = subExpression.interpret();
        			subExpression = new Variable(firstString);
    				expressionStackNew.push(subExpression);
        			
        		}
        		else{
        			subExpression = new Variable(firstString);
        			expressionStackNew.push(subExpression);
        			subExpression = new Variable(secondString);
        			expressionStackNew.push(subExpression);
        		}
        	}
        	else{
        		subExpression = new Variable(firstString);
    			expressionStackNew.push(subExpression);
        	}
        		
        }
		
        expressionStack = expressionStackNew;
        while(!expressionStack.isEmpty())
        {
        	tempExpressionStack.push(expressionStack.pop());
        }
        expressionStack = tempExpressionStack; 
        
        syntaxTree = expressionStack.pop();
        String fullQuery = syntaxTree.interpret();
        
        while(!expressionStack.isEmpty())
        {
        	fullQuery = fullQuery + " " + expressionStack.pop().interpret();
        }
        
        
        
		syntaxTree = new Variable(fullQuery);
		
	}
	
	
	public String interpret(){
		
		
		return syntaxTree.interpret();
		
	}
	private String CheckField(String word){
	
		int index = 0;
		String tempField = null;
		
		if(word.contains("(") && word.contains(":")){
			
			  index = word.indexOf(":");
			
			field = word.substring(0, index);
			return field;
		}
		if(word.contains(")")){
			tempField = field;
			field = "Term";
			return tempField;
		}
		else if(word.contains(":"))
		{
            index = word.indexOf(":");
			field = word.substring(0, index);
		}
		
		
		return field;
		
	}
	
}
