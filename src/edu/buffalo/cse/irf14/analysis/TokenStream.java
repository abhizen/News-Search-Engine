/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 *          Class that represents a stream of Tokens. All
 *         {@link Analyzer} and {@link TokenFilter} instances operate on this to
 *         implement their behavior
 */
public class TokenStream implements Iterator<Token> {
	private int current;
	private int maxLength = 0;
	private ArrayList<Token> TokenArrayList;
	private int flag = 0;
	public long time =0;

	TokenStream(Token[] itemList) {
		if (itemList.length > 0) {
			current = -1;
			TokenArrayList = new ArrayList<Token>(Arrays.asList(itemList));
			this.maxLength = TokenArrayList.size();
			flag = 0;
		}
	}

	/**
	 * Flag is set when token is deleted.
	 */
	protected void setFlag() {
		flag = 1;
	}

	protected void resetFlag() {
		flag = 0;
	}

	protected boolean testFlag() {
		return flag == 1;
	}

	public int getStatus() {
		return current;
	}

	public void setValue(Token tokenItem, int index) {
		TokenArrayList.set(index, tokenItem);
	}

	public Token getToken() {
		return TokenArrayList.get(current);
	}

	public void modifyToken(Token tempToken) {
		TokenArrayList.set(current, tempToken);
	}

	public ArrayList<Token> getList() {
		return TokenArrayList;
	}

	public Token getToken(int index) {
		if(index<TokenArrayList.size())
			return TokenArrayList.get(index);
		else
			return null;
	}

	/**
	 * Method that checks if there is any Token left in the stream with regards
	 * to the current pointer. DOES NOT ADVANCE THE POINTER
	 * 
	 * @return true if at least one Token exists, false otherwise
	 */
	@Override
	public boolean hasNext() {
		if (TokenArrayList.size() == 0 || current >= TokenArrayList.size() - 1)
			return false;
		else
			return true;
	}

	/**
	 * Method to return the next Token in the stream. If a previous hasNext()
	 * call returned true, this method must return a non-null Token. If for any
	 * reason, it is called at the end of the stream, when all tokens have
	 * already been iterated, return null
	 */
	@Override
	public Token next() {
		Token tokenItem;
		if (TokenArrayList.size() > 0 && current < TokenArrayList.size() - 1) {
			if (this.testFlag() && current != -1) {
				tokenItem = TokenArrayList.get(current);
				this.resetFlag();
			} else {
				current++;
				this.resetFlag();
				tokenItem = TokenArrayList.get(current);
			}
		} else if (current == TokenArrayList.size() - 1 && this.testFlag()
				&& current > -1) {
			this.resetFlag();
			tokenItem = TokenArrayList.get(current);
			current++;
		} else {
			current++;
			this.resetFlag();
			return null;
		}
		return tokenItem;
	}

	/**
	 * Method to remove the current Token from the stream. Note that "current"
	 * token refers to the Token just returned by the next method. Must thus be
	 * NO-OP when at the beginning of the stream or at the end
	 */
	@Override
	public void remove() {
		if (current >= 0 && current < TokenArrayList.size() && !this.testFlag()) {
			TokenArrayList.remove(current);
			current--;
			this.setFlag();
			TokenArrayList.trimToSize();
		}
	}

	public void removeToken(int index) {
		TokenArrayList.remove(index);
		this.setFlag();
		TokenArrayList.trimToSize();
	}

	/**
	 * Method to reset the stream to bring the iterator back to the beginning of
	 * the stream. Unless the stream has no tokens, hasNext() after calling
	 * reset() must always return true.
	 */
	public void reset() {
		current = -1;
		this.resetFlag();
	}

	/**
	 * Method to append the given TokenStream to the end of the current stream
	 * The append must always occur at the end irrespective of where the
	 * iterator currently stands. After appending, the iterator position must be
	 * unchanged Of course this means if the iterator was at the end of the
	 * stream and a new stream was appended, the iterator hasn't moved but that
	 * is no longer the end of the stream.
	 * 
	 * @param stream
	 *            : The stream to be appended
	 */
	public void append(TokenStream stream) {
		if (stream != null && stream.getList().size() > 0)
			TokenArrayList.addAll(stream.getList());
	}

	public Token getCurrent() {
		// TODO: YOU MUST IMPLEMENT THIS
		if (current >= TokenArrayList.size() || current == -1
				|| this.testFlag())
			return null;
		else
			return TokenArrayList.get(current);
	}

}
