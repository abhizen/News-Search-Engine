package edu.buffalo.cse.irf14.analysis;

import java.util.ArrayList;

import edu.buffalo.cse.irf14.document.FieldNames;

public class FieldChainedAnalyzer implements Analyzer {
	protected TokenStream tokenList;
	protected Token tokenItem;
	protected FieldNames tempNames;
	protected TypeTokenFilter listFilter;
	protected ArrayList<String> filterList;
	int removeFlag = 0;

	public FieldChainedAnalyzer(FieldNames name, TokenStream stream) {
		tokenList = stream;
		tempNames = name;
		filterList = new ArrayList<String>();
		switch (name) {
		case CONTENT:
			filterList.add("SYMBOL");
			filterList.add("SPECIALCHARS");
			filterList.add("ACCENT");
			filterList.add("CAPITALIZATION");
			filterList.add("DATE");
			filterList.add("STOPWORD");
			filterList.add("STEMMER");
			filterList.add("NUMERIC");
			break;
		case AUTHOR:
			filterList.add("CAPITALIZATION");
			filterList.add("NUMERIC");
			filterList.add("STOPWORD");
			break;
		case AUTHORORG:
			filterList.add("CAPITALIZATION");
			filterList.add("NUMERIC");
			filterList.add("STOPWORD");
			break;
		case CATEGORY:
			filterList.add("SYMBOL");
			break;
		case FILEID:
			filterList.add("SYMBOL");
			break;
		case NEWSDATE:
			filterList.add("SPECIALCHARS");
			filterList.add("SYMBOL");
			filterList.add("DATE");
			break;
		case PLACE:
			filterList.add("SPECIALCHARS");
			filterList.add("SYMBOL");
			filterList.add("CAPITALIZATION");
			filterList.add("NUMERIC");
			filterList.add("STOPWORD");
			break;
		case TITLE:
			filterList.add("SYMBOL");
			filterList.add("SPECIALCHARS");
			filterList.add("ACCENT");
			filterList.add("CAPITALIZATION");
			filterList.add("DATE");
			filterList.add("STOPWORD");
			filterList.add("STEMMER");
			filterList.add("NUMERIC");
			break;
		default:
			break;
		}
	}

	public boolean next(TokenFilterType tempType) throws TokenizerException {
		Token tempToken;
		if (this.tokenList.getStatus() > -1
				&& tokenList.getStatus() < tokenList.getList().size()) {
			tempToken = this.tokenList.getCurrent();
			if ((tempToken == null) || tempToken.getTermText() == null
					|| tempToken.getTermText().length() == 0)
				this.tokenList.remove();
			else
				this.tokenList = this.listFilter.process(tokenList, tempType);
			return true;
		} else
			return false;
	}

	public boolean increment() throws TokenizerException {
		Boolean flag = true;
		TokenFilterFactory tempFactory = TokenFilterFactory.getInstance();
		Token tempToken = null;
		if (tokenList.hasNext())
			tempToken = tokenList.next();
		else if (tokenList.hasNext() == false && tokenList.testFlag() == true) {
			tokenList.resetFlag();
			tempToken = tokenList.getCurrent();
		} else
			return false;
		if ((tempToken == null) || tempToken.getTermText() == null
				|| tempToken.getTermText().length() == 0) {
			this.tokenList.remove();
			return true;
		}

		for (String filtertype : filterList) {
			if (filtertype == "SPECIALCHARS") {
				listFilter = tempFactory.getFilterByType(
						TokenFilterType.SPECIALCHARS, tokenList);
				if (this.next(TokenFilterType.SPECIALCHARS))
					flag = true;
				else
					flag = false;
				if (this.tokenList.testFlag() == true) {
					flag = true;
					break;
				}
				continue;
			}
			if (filtertype == "SYMBOL") {
				// tokenList.reset();
				listFilter = tempFactory.getFilterByType(
						TokenFilterType.SYMBOL, tokenList);
				if (this.next(TokenFilterType.SYMBOL))
					flag = true;
				else
					flag = false;
				if (this.tokenList.testFlag() == true) {
					flag = true;
					break;
				}
				continue;
			}
			if (filtertype == "ACCENT") {
				// tokenList.reset();
				listFilter = tempFactory.getFilterByType(
						TokenFilterType.ACCENT, tokenList);
				if (this.next(TokenFilterType.ACCENT))
					flag = true;
				else
					flag = false;

				if (this.tokenList.testFlag() == true) {
					flag = true;
					break;
				}
				continue;
			}
			if (filtertype == "CAPITALIZATION") {
				// tokenList.reset();
				listFilter = tempFactory.getFilterByType(
						TokenFilterType.CAPITALIZATION, tokenList);

				if (this.next(TokenFilterType.CAPITALIZATION))
					flag = true;
				else
					flag = false;
				if (this.tokenList.testFlag() == true) {
					flag = true;
					break;
				}
				continue;
			}
			if (filtertype == "DATE") {
				// tokenList.reset();
				long startTime = System.nanoTime();
				listFilter = tempFactory.getFilterByType(TokenFilterType.DATE,
						tokenList);
				if (this.next(TokenFilterType.DATE))
					flag = true;
				else
					flag = false;
				if (this.tokenList.testFlag() == true) {
					flag = true;
					break;
				}
				long endTime = System.nanoTime();
				tokenList.time = endTime - startTime;
				
				continue;
			}
			if (filtertype == "STEMMER") {
				// tokenList.reset();
				listFilter = tempFactory.getFilterByType(
						TokenFilterType.STEMMER, tokenList);
				if (this.next(TokenFilterType.STEMMER))
					flag = true;
				else
					flag = false;
				if (this.tokenList.testFlag() == true) {
					flag = true;
					break;
				}
				continue;
			}
			if (filtertype == "NUMERIC") {
				// tokenList.reset();
				listFilter = tempFactory.getFilterByType(
						TokenFilterType.NUMERIC, tokenList);
				if (this.next(TokenFilterType.NUMERIC))
					flag = true;
				else
					flag = false;
				if (this.tokenList.testFlag() == true) {
					flag = true;
					break;
				}
				continue;
			}
			if (filtertype == "STOPWORD") {
				// tokenList.reset();
				listFilter = tempFactory.getFilterByType(
						TokenFilterType.STOPWORD, tokenList);
				if (this.next(TokenFilterType.STOPWORD))
					flag = true;
				else
					flag = false;
				if (this.tokenList.testFlag() == true) {
					flag = true;
					break;
				}
				continue;
			}

		}
		return flag;
	}

	/**
	 * Return the underlying {@link TokenStream} instance
	 * 
	 * @return The underlying stream
	 */
	// TERM, PLACE, AUTHOR or CATEGORY
	public TokenStream getStream() {
		return this.tokenList;
	}

	public void Filter() throws TokenizerException {
		tokenList.reset();
		while (increment()) {
		}
	}

}