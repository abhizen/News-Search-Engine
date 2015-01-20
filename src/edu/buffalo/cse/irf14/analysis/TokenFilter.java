/**
 *
 */

package edu.buffalo.cse.irf14.analysis;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.buffalo.cse.org.tartarus.snowball.ext.porterStemmer;

/**
 * The abstract class that you must extend when implementing your TokenFilter
 * rule implementations. Apart from the inherited Analyzer methods, we would use
 * the inherited constructor (as defined here) to test your code.
 * 
 * 
 * 
 */
public abstract class TokenFilter implements Analyzer {
	protected TokenStream tokenList;
	ArrayList<String> stopList;
	Pattern yearPattern = null;
	Pattern yearPattern1 = null;
	Pattern christYearPattern = null;
	Pattern christYearPattern1 = null;
	Pattern adbcDate = null;
	Pattern timeFormat = null;
	Pattern timeFormat1 = null;
	Pattern numericPattern = null;
	Pattern stemmerPattern = null;
	Pattern finalDateFormat = null;
	NumericFilter numFilter = null;
	static long timeTaken =0;
	DateFilter datefilter = null;
	
	private Map<String, String> contractionsMap;

	/**
	 * Default constructor, creates an instance over the given TokenStream
	 * 
	 * @param stream
	 *            : The given TokenStream instance
	 */
	public TokenFilter(TokenStream stream) {
		// TODO : YOU MUST IMPLEMENT THIS METHOD
		tokenList = stream;
		yearPattern = Pattern
				.compile("([\\s]*[12][089][0-9][0-9]([\\-][0-9]?[0-9])?[\\.]?[\\,]?[\\s]*)");
		yearPattern1 = Pattern
				.compile("([\\s]*[12][089][0-9][0-9][\\.]?[\\,]?[\\s]*)");
		christYearPattern = Pattern
				.compile("([\\s]*[0-9]*[\\s]*(BC)?(AD)?[\\.]?[\\,]?[\\s]*)");
		christYearPattern1 = Pattern.compile("([\\s]*(BC)?(AD)?[\\.]?[\\,]?$)");
		adbcDate = Pattern.compile("([\\s]*[0-9]*[\\.]?[\\,]?[\\s]*)");
		timeFormat = Pattern
				.compile("([\\s]*([1]?[0-9]?[:][1]?[0-9]?[\\s]*)*((AM)?(PM)?(am)?(pm)?)[\\.]?[\\,]?[\\s]*)");
		timeFormat1 = Pattern
				.compile("([\\s]*(AM)?(PM)?(am)?(pm)?[\\.]?[\\,]?[\\s]*)");
		/*numericPattern = Pattern
				.compile("(([\\\"]*[\\s]*[\0]*[\\.]*[0-9]*[\\,]?[\\-]*[\\/]?[\\s]*[0-9]*[\0]*[\\\"]*)*([\\\"]*[\0]*[\\.]*[0-9]*[\\,]?[\\-]*[\\/]?[\\s]*[0-9]*[\0]*[\\s]*[\\\"]*)$)|([\\\"]*[\0]*[\\.]*[0-9]*[\\.]?[\\s]*[0-9]*[\\%]?[\\.]*[\0]*[\\\"]*)");*/
		//numericPattern = Pattern.compile("([\\\"]*[\\s]*[\0]*[\\.]*[0-9]*[\\,]?[\\-]*[\\/]?[\\s]*[0-9]*[\0]*[\\\"]*)+");
		stemmerPattern = Pattern.compile("([^a-zA-Z]+[a-zA-Z]*$)");
		finalDateFormat = Pattern
				.compile("([\\s]*[\\-]?[0-9][0-9][0-9][0-9][0-1][0-9][0-2][0-9][\\.]?[\\,]?[\\s]*)");
		numFilter = numFilter.getInstance();
		datefilter = new DateFilter();
	}

	public boolean checkDateFormat(String input) {
		String[] tempMonth = { "jan", "feb", "mar", "apr", "may", "jun", "jul",
				"aug", "sept", "oct", "nov", "dec" };
		String[] tempFullMonth = { "january", "february", "march", "april",
				"may", "june", "july", "august", "september", "october",
				"november", "december" };
		Pattern finalDateFormat = Pattern
				.compile("([\\s]*[12][0-9][0-9][0-9][0-1][0-9][0-2][0-9][\\.]?[\\,]?[\\s]*)");
		int index = 0;

		for (String temStr : tempMonth) {
			if (input.toLowerCase().contains(temStr)
					|| input.toLowerCase().contains(tempFullMonth[index]))
				return true;
			index++;
		}

		if (yearPattern.matcher(input).matches())
			return true;
		if (yearPattern1.matcher(input).matches())
			return true;
		if (christYearPattern.matcher(input).matches())
			return true;
		if (christYearPattern1.matcher(input).matches())
			return true;
		if (timeFormat.matcher(input).matches())
			return true;
		if (timeFormat1.matcher(input).matches())
			return true;
		if (finalDateFormat.matcher(input).matches())
			return true;
		else
			return false;
	}

	public TokenStream setNULL(TokenStream stream, int index) {
		stream.removeToken(index);
		return stream;
	}

	public TokenStream process(TokenStream tempTokenStream,
			TokenFilterType tokenType) throws TokenizerException {
		Pattern ptr, ptr1;
		Matcher matcher;
		String PATTERN = null;
		String PATTERN1 = null;
		String input = tempTokenStream.getCurrent().getTermText();
		Token newToken = new Token();
		String day = null;
		int commaFlag = 0;
		int tokenStatus = 0;
		String prevString = null;
		String nextString = null;
		
		if (!(input!=null && input.trim().length()>0)){
			return tempTokenStream;
		}

		switch (tokenType) {
		case SYMBOL:
			/*
			 * Below code is to remove special characters .,!,? Dheeraj - below
			 * code removes any special character at the end of the sentence, .
			 * ! ? included
			 */
			
			
			if (input!=null && input.trim().length()>1 && input.indexOf('\'') == 0
					&& input.lastIndexOf('\'') == input.length() - 1) {
				input = input.substring(1, input.length() - 1);
			}
			
			 
			
			tokenStatus = tempTokenStream.getStatus();

			for (int i = input.length() - 1; i > 0; i--) {
				char a = input.charAt(i);
				String aString = Character.toString(a);
				String aStringPrev = null;
				if (i > 1) {
					aStringPrev = Character.toString(input.charAt(i - 1));
				}
				if (input!=null && input.trim().length()>1 && i > 1 && ((aStringPrev + aString).equals("'s"))) {
					input = input.substring(0, i - 1);
					i--;
				} else if (i > 1 && ((aStringPrev + aString).equals("s'"))) {
					input = input.substring(0, i - 1) + "s";
					break;
				} else if (aString.matches("[a-zA-Z0-9]")) {
					break;
				} else if (aString.equals(".") || aString.equals("!")
						|| aString.equals("?") || aString.equals(",")) {
					input = input.substring(0, i);
				}
			}

			if (ContractionsCollection.populateCOntractionMap().get(input) != null)
				input = ContractionsCollection.populateCOntractionMap().get(
						input);

			PATTERN = "([\\s]*[\\-]+[\\s]*$)";
			ptr = Pattern.compile(PATTERN);
			matcher = ptr.matcher(input);

			if (matcher.matches() == true) {
				if (tokenStatus > 0)
					prevString = tempTokenStream.getToken(tokenStatus - 1)
							.getTermText();
				if (tokenStatus < tempTokenStream.getList().size() - 1) {
					nextString = tempTokenStream.getToken(tokenStatus + 1)
							.getTermText();
				}
				PATTERN = "([\\s]*[A-Za-z]+[\\s]*$)";
				ptr = Pattern.compile(PATTERN);

				if (prevString != null && nextString != null
						&& ptr.matcher(prevString).matches()
						&& ptr.matcher(nextString).matches())
					input = "";
			}

			PATTERN = "([\\s]*[A-Za-z]+[\\s]*[\\-]+[\\s]*[A-Za-z]+[\\s]*$)";
			ptr = Pattern.compile(PATTERN);
			matcher = ptr.matcher(input);

			if (matcher.matches() == true)
				input = input.replace('-', ' ');
			else {
				// PATTERN="(^[\\-].*)|(.*[\\-]$)";
				PATTERN = "(^[\\-]+[^\\-]*)|([^\\-]*[\\-]+$)";
				ptr = Pattern.compile(PATTERN);
				matcher = ptr.matcher(input);
				if (matcher.matches() == true)
					input = input.replaceAll("[\\-]", "");
			}

			/**
			 * Code to check if string is enclosed by single quotes
			 */
			if (tokenStatus > 0)
				prevString = tempTokenStream.getToken(tokenStatus - 1)
						.getTermText();
			if (tokenStatus < tempTokenStream.getList().size() - 1)
				nextString = tempTokenStream.getToken(tokenStatus + 1)
						.getTermText();
			PATTERN = "([\\'][^\\']*$)";
			ptr = Pattern.compile(PATTERN);

			if (input != null && input.length() > 0
					&& ptr.matcher(input).matches() && nextString != null
					&& nextString.length() > 0) {
				PATTERN = "([^\\']*[\\']$)";
				ptr = Pattern.compile(PATTERN);
				if (ptr.matcher(nextString).matches()) {
					input = input.substring(1, input.length());
					nextString = nextString.substring(0,
							nextString.length() - 1);
					newToken.setTermText(nextString);
					tempTokenStream.setValue(newToken,
							tempTokenStream.getStatus() + 1);
				}
			}

			PATTERN = "([^\\']*[\\']$)";
			ptr = Pattern.compile(PATTERN);

			if (input != null && input.length() > 0
					&& ptr.matcher(input).matches() && prevString != null
					&& prevString.length() > 0) {
				PATTERN = "([\\'][^\\']*$)";
				ptr = Pattern.compile(PATTERN);
				if (ptr.matcher(prevString).matches()) {
					input = input.substring(0, input.length() - 1);
					prevString = prevString.substring(1, prevString.length());
					newToken.setTermText(prevString);
					tempTokenStream.setValue(newToken,
							tempTokenStream.getStatus() - 1);
				}
			}

			/**
			 * To decode single quotes as derivative
			 */
			PATTERN = "([A-Za-z][\\']+[\\(][a-z][\\)]$)";
			ptr = Pattern.compile(PATTERN);
			if (input != null && input.length() > 0
					&& ptr.matcher(input).matches())
				input = input.replaceAll("[\\']", "");
			PATTERN = "([d][a-z][\\']+[\\/][d][a-z]$)";
			ptr = Pattern.compile(PATTERN);
			if (input != null && input.length() > 0
					&& ptr.matcher(input).matches())
				input = input.replaceAll("[\\']", "");
			newToken = new Token();
			newToken.setTermText(input);
			tempTokenStream.setValue(newToken, tempTokenStream.getStatus());

			break;
		case SPECIALCHARS:
			//long startTime = System.nanoTime();
			
			PATTERN = "(([\\s]*[^\\%\\$\\@\\~\\+\\<\\>\\'\\/\\,\\\"\\)\\(\\&\\#\\;]*[\\s]*[\\%\\$\\@\\~\\+\\<\\>\\'\\/\\,\\\"\\)\\(\\&\\#\\;]*[\\s]*[^\\%\\$\\@\\~\\+\\<\\>\\'\\/\\,\\\"\\)\\(\\&\\#\\;]*[\\s]*)*)";
			ptr = Pattern.compile(PATTERN);
			matcher = ptr.matcher(input);

			if (matcher.matches() == true) {
				// input.replaceAll("(\\!$)|(\\.$)|(\\?$)|(\\'$)|(\\'s$)", "");
				input = input.replaceAll("\\+", "");
				input = input.replaceAll("<", "");
				input = input.replaceAll(">", "");
				input = input.replaceAll("'", "");
				input = input.replaceAll("/", "");
				input = input.replaceAll(",", "");

				input = input.replaceAll("\"", "");
				input = input.replaceAll("\\)", "");
				input = input.replaceAll("\\(", "");
				input = input.replaceAll("[\\~]", "");
				input = input.replaceAll("&", "");
				input = input.replaceAll("#", "");
				input = input.replaceAll(";", "");
				input = input.replaceAll("@", "");
				input = input.replaceAll("#", "");
				input = input.replaceAll("[\\$]", "");
				input = input.replaceAll("%", "");
				newToken.setTermText(input);
				tempTokenStream.setValue(newToken, tempTokenStream.getStatus());
			}

			PATTERN = "(([\\s]*[^\\*\\^\\=\\:\\;\\|\\_\\\\]*[\\*\\^\\=\\:\\;\\|\\_\\\\]*[^\\*\\^\\=\\:\\;\\|\\_\\\\]*[\\s]*)*)";
			ptr = Pattern.compile(PATTERN);
			matcher = ptr.matcher(input);

			if (matcher.matches()) {
				input = input.replaceAll("[\\*]", "");
				input = input.replaceAll("[\\^]", "");
				input = input.replaceAll("[\\=]", "");
				input = input.replaceAll("[\\:]", "");
				input = input.replaceAll("[\\;]", "");
				input = input.replaceAll("[\\|]", "");
				input = input.replaceAll("[\\_]", "");
				input = input.replaceAll("[\\\\]", "");
				newToken.setTermText(input);
				tempTokenStream.setValue(newToken, tempTokenStream.getStatus());
			}

			PATTERN = "([A-Za-z]+[\\s]*[\\-][\\s]*[A-Za-z]+$)";
			ptr = Pattern.compile(PATTERN);

			if (ptr.matcher(input).matches()) {
				input = input.replaceAll("[\\-]", "");
				newToken.setTermText(input);
				tempTokenStream.setValue(newToken, tempTokenStream.getStatus());
			}
			
			
			break;
		case DATE:
			try
			{
			datefilter.process(tempTokenStream, input);
			}
			catch(Exception e)
			{
				System.out.println("date filter exception");
				e.printStackTrace();
				
			}
			
			break;

		case NUMERIC:
			
			if (numFilter.checkNumber(input) == true) {
				PATTERN = "[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][\\s][0-9][0-9][\\:][0-9][0-9][\\:][0-9][0-9]";
				ptr = Pattern.compile(PATTERN);
				matcher = ptr.matcher(input);
				tokenStatus = tempTokenStream.getStatus();
				/**
				 * To check if previous or next string is in date format
				 */
				if (tokenStatus > 0)
					prevString = tempTokenStream.getToken(tokenStatus - 1)
							.getTermText();
				if (tokenStatus < tempTokenStream.getList().size() - 1)
					nextString = tempTokenStream.getToken(tokenStatus + 1)
							.getTermText();

				if (prevString != null && prevString.length() > 0
						&& checkDateFormat(prevString))
					break;
				if (nextString != null && nextString.length() > 0
						&& checkDateFormat(nextString))
					break;
				if (input != null && input.length() > 0
						&& finalDateFormat.matcher(input).matches())
					break;

				if (matcher.matches() == false) {
					if (input.contains("/"))
						input = "/";
					else if (input.contains("%"))
						input = "%";
					else {
						input = "";
						/* setNull function sets text as NULL for the token */
						tempTokenStream = this.setNULL(tempTokenStream,
								tempTokenStream.getStatus());
						break;
					}
					newToken.setTermText(input);
					tempTokenStream.setValue(newToken,
							tempTokenStream.getStatus());
				}
			}

			
			break;

		case CAPITALIZATION:
			PATTERN = "[A-Z]*";
			ptr = Pattern.compile(PATTERN);
			tokenStatus = tempTokenStream.getStatus();
			matcher = ptr.matcher(input);
			int streamSize = tempTokenStream.getList().size();

			PATTERN1 = "((([A-Z][a-z]*)*([\\s])*)*([A-Z][a-z]*)$)";
			if (Pattern.compile(PATTERN1).matcher(input).matches() == true) {
				if (tempTokenStream.getStatus() > 0)
					prevString = tempTokenStream.getToken(tokenStatus - 1)
							.getTermText();
				if (tempTokenStream.getStatus() < streamSize - 1)
					nextString = tempTokenStream.getToken(tokenStatus + 1)
							.getTermText();
				PATTERN = "(([A-Z][a-z]*)*([\\s])*([A-Z][a-z]*)$)";
				ptr = Pattern.compile(PATTERN);
				matcher = ptr.matcher(input);
				PATTERN1 = "(.*[\\.]$)";
				/* Condition to check if taken is first word of a sentence */
				if (prevString == null
						|| Pattern.compile(PATTERN1).matcher(prevString)
								.matches() == true) {
					if (nextString != null
							&& ptr.matcher(nextString).matches() == false) {
						input = input.toLowerCase();
						newToken.setTermText(input);
						tempTokenStream.setValue(newToken,
								tempTokenStream.getStatus());
					} else if (nextString != null) {
						Token tempToken = new Token();
						tempToken.setTermText(input + " " + nextString);
						tempTokenStream.setValue(tempToken,
								tempTokenStream.getStatus() + 1);

						/* setNull function sets text as NULL for the token */
						tempTokenStream = this.setNULL(tempTokenStream,
								tempTokenStream.getStatus());
					}
				} else if (prevString != null
						&& ptr.matcher(prevString).matches() == true) {
					Token tempToken = new Token();
					tempToken.setTermText(prevString + " " + input);
					tempTokenStream.setValue(tempToken,
							tempTokenStream.getStatus());
					/* setNull function sets text as NULL for the token */
					tempTokenStream = this.setNULL(tempTokenStream,
							tempTokenStream.getStatus() - 1);
				} else if (nextString != null
						&& ptr.matcher(nextString).matches() == true) {
					Token tempToken = new Token();
					tempToken.setTermText(input + " " + nextString);
					tempTokenStream.setValue(tempToken,
							tempTokenStream.getStatus() + 1);
					/* setNull function sets text as NULL for the token */
					tempTokenStream = this.setNULL(tempTokenStream,
							tempTokenStream.getStatus());
				}
			}
			break;

		case STEMMER:

			if (!stemmerPattern.matcher(input).matches()) {
				porterStemmer stemmer = new porterStemmer();
				// set the word to be stemmed
				stemmer.setCurrent(input);
				if (stemmer.stem())
					// If stemming is successful obtain the stem of the given
					// word
					input = stemmer.getCurrent();
			}
			newToken.setTermText(input);
			tempTokenStream.setValue(newToken, tempTokenStream.getStatus());
			break;

		case STOPWORD:
			for (String tempStr : StopWordsList.getStopList()) {
				if (input.trim().toLowerCase().equals(tempStr)) {
					tempTokenStream = this.setNULL(tempTokenStream,
							tempTokenStream.getStatus());
					break;
				}
			}
			break;

		case ACCENT:
			AccentRemoval accString = new AccentRemoval(tokenList.getToken());
			Token tempToken = accString.remove();
			tempTokenStream.setValue(tempToken, tempTokenStream.getStatus());
		}
		return tempTokenStream;
	}
}