/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

/**
 * Factory class for instantiating a given TokenFilter
 * 
 * 
 * 
 */
public class TokenFilterFactory {
	/**
	 * Static method to return an instance of the factory class. Usually factory
	 * classes are defined as singletons, i.e. only one instance of the class
	 * exists at any instance. This is usually achieved by defining a private
	 * static instance that is initialized by the "private" constructor. On the
	 * method being called, you return the static instance. This allows you to
	 * reuse expensive objects that you may create during instantiation
	 * 
	 * @return An instance of the factory
	 */

	private static TokenFilterFactory instance;

	public static TokenFilterFactory getInstance() {
		if (instance == null)
			instance = new TokenFilterFactory();
		return instance;
	}

	/**
	 * Returns a fully constructed {@link TokenFilter} instance for a given
	 * {@link TokenFilterType} type
	 * 
	 * @param type
	 *            : The {@link TokenFilterType} for which the
	 *            {@link TokenFilter} is requested
	 * @param stream
	 *            : The TokenStream instance to be wrapped
	 * @return The built {@link TokenFilter} instance
	 */
	public TypeTokenFilter getFilterByType(TokenFilterType type,
			TokenStream stream) {
		TypeTokenFilter tempFilter;
		TokenFilterType tempType = type;
		TokenStream tempStream = stream;
		if (tempType == null)
			return null;
		else
			tempFilter = new TypeTokenFilter(tempType, tempStream);
		return tempFilter;
	}
}
