package edu.buffalo.cse.irf14.analysis;

public class TypeTokenFilter extends TokenFilter {
	private TokenFilterType tokenType;

	TypeTokenFilter(TokenFilterType type, TokenStream stream) {
		super(stream);
		tokenType = type;
	}

	public TokenStream getStream() {
		return super.tokenList;
	}

	public boolean increment() throws TokenizerException {
		Token tempToken = null;
		if (tokenList.hasNext()) {
			tempToken = tokenList.next();

			if ((tempToken == null) || tempToken.getTermText() == null
					|| tempToken.getTermText().length() == 0)
				tokenList.remove();
			else
				tokenList = process(tokenList, tokenType);
			return true;
		} else if (tokenList.hasNext() == false && tokenList.testFlag() == true) {
			tokenList.resetFlag();
			tempToken = tokenList.getCurrent();

			if ((tempToken == null) || tempToken.getTermText() == null
					|| tempToken.getTermText().length() == 0)
				tokenList.remove();
			else
				tokenList = process(tokenList, tokenType);
			return true;
		} else
			return false;
	}

	public void Filter() throws TokenizerException {
		Token tempToken;
		try {
			tokenList.reset();
			for (int index = 0; index <= tokenList.getList().size() - 1; index++) {
				tempToken = tokenList.getToken(index);
				if ((tempToken != null) && tempToken.getTermText() != null
						&& tempToken.getTermText().length() > 0)
					this.increment();
				else
					tokenList.removeToken(index);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new TokenizerException(
					"Unexpected error occured during indexing.");
		}
	}
}
