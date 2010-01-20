/**
 * 
 */
package de.tixus.eopac.server;

import static org.testng.Assert.assertNotNull;

import org.testng.annotations.Test;

import de.tixus.eopac.domain.Isbn13;
import de.tixus.eopac.domain.MediaEntry;
import de.tixus.eopac.server.zones.LibraryLookupZonesImpl;

/**
 * @author TSP
 * 
 */
@Test
public class HtmlProtocolTest {

  public void testStaticHtmlSearch() throws Exception {

    final LibraryLookup libraryLookup = new LibraryLookupZonesImpl();
    final Isbn13 isbnNumber = new Isbn13("978-0-330-32002-3");
    final MediaEntry mediaEntry = libraryLookup.findByIsbn(isbnNumber);

    assertNotNull(mediaEntry);
  }
}
