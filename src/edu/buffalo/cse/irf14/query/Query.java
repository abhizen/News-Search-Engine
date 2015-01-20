package edu.buffalo.cse.irf14.query;

/**
 * Class that represents a parsed query
 * @author nikhillo
 *
 */
public class Query {
	private String parsedQuery;
	
	public Query(String userQuery)
	{
		if(userQuery != null && userQuery.length()>0)
		  parsedQuery = userQuery;
	}
	/**
	 * Method to convert given parsed query into string
	 */
	public String toString() {
		//TODO: YOU MUST IMPLEMENT THIS
		if(parsedQuery!=null && parsedQuery.length()>0) 
		  return parsedQuery;
		else
		  return null;
	}
}
