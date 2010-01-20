/**
 * 
 */
package de.tixus.eopac.client;

/**
 * @author TSP
 * 
 */
public class Isbn13Validator {
	/*
	 * stolen
	 * fromhttp://svn.apache.org/viewvc/jakarta/commons/proper/validator/trunk
	 * /src
	 * /share/org/apache/commons/validator/routines/ISBNValidator.java?revision
	 * =487479&view=markup&pathrev=488896
	 */
	private static final String SEP = "(?:\\-|\\s)";
	private static final String GROUP = "(\\d{1,5})";
	private static final String PUBLISHER = "(\\d{1,7})";
	private static final String TITLE = "(\\d{1,6})";
	/**
	 * ISBN-13 consists of 5 groups of numbers separated by either dashes (-) or
	 * spaces. The first group is 978 or 979, the second group is 1-5
	 * characters, third 1-7, fourth 1-6, and fifth is 1 digit.
	 */
	static final String ISBN13_REGEX = "^(978|979)(?:(\\d{10})|(?:" + SEP
			+ GROUP + SEP + PUBLISHER + SEP + TITLE + SEP + "([0-9])))$";

	public boolean isValid(final String isbn) {
		return !"".equals(isbn) && isbn.matches(ISBN13_REGEX);
	}
}
