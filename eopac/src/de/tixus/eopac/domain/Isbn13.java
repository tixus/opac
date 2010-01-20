/**
 * 
 */
package de.tixus.eopac.domain;

import de.tixus.eopac.client.Isbn13Validator;

/**
 * @author TSP
 * 
 */
public class Isbn13 {
	private final String number;
	// TODO move into factory
	private final Isbn13Validator validator = new Isbn13Validator();

	public Isbn13(final String isbnNumber) {
		if (!validator.isValid(isbnNumber)) {
			throw new IllegalArgumentException("ISBN not valid:" + isbnNumber);
		}
		this.number = isbnNumber;
	}

	@Override
	public String toString() {
		return number;
	};
}
