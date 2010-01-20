package de.tixus.eopac.server;

import de.tixus.eopac.domain.Isbn13;
import de.tixus.eopac.domain.MediaEntry;

public interface LibraryLookup {

  public MediaEntry findByIsbn(final Isbn13 isbnNumber) throws Exception;

}