package com.cyto.bargainbooks.factory.book;

import com.cyto.bargainbooks.model.Book;

/**
 * This interface will be implemented by all the factories.
 */
public interface BookFactory {

    /**
     * Returns a {@link Book} entity if the necessary informations ara available in the given {@link String}, if the Title, Author or ISBN is not available, it will return null
     *
     * @param s The HTML page content
     * @return the {@link Book} entity
     */
    Book createBook(String s);
}
