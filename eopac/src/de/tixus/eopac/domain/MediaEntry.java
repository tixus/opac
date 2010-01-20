/**
 * 
 */
package de.tixus.eopac.domain;

import java.util.List;

/**
 * @author TSP
 * 
 */
public interface MediaEntry {

	List<Location> getAvailableLocations();

	String getTitle();

	boolean isAvailable();
}
