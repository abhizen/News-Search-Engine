/**
 * 
 */
package edu.buffalo.cse.irf14.query;

/**
 * @author nikhillo
 * Static parser that converts raw text to Query objects
 */
public class QueryParser {
	
	/**
	 * MEthod to parse the given user query into a Query object
	 * @param userQuery : The query to parse
	 * @param defaultOperator : The default operator to use, one amongst (AND|OR)
	 * @return Query object if successfully parsed, null otherwise
	 */
	public static Query parse(String userQuery, String defaultOperator) {
		//TODO: YOU MUST IMPLEMENT THIS METHOD
		String expression = null;
		String defOperator =null;
		if(userQuery!=null && userQuery.length()>0 )
		   expression = userQuery;
		else
			return null;
		
		if(defaultOperator!=null && defaultOperator.length()>0 )
			defOperator = defaultOperator;
			else
				return null;
		
		Evaluator sentence = new Evaluator(expression,defOperator);
		expression = sentence.interpret();
		Query userQueryObj = new Query(expression);
		return userQueryObj;
	}
}
