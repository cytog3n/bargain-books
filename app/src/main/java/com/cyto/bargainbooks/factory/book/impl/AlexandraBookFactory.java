package com.cyto.bargainbooks.factory.book.impl;

import com.cyto.bargainbooks.factory.book.BookFactory;
import com.cyto.bargainbooks.model.Book;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * BookFactory for @url{https://alexandra.hu}
 *
 * @see BookFactory
 */
public class AlexandraBookFactory implements BookFactory {

    /**
     * @see BookFactory#createBook(String)
     */
    @Override
    public Book createBook(String s) {
        Book b = new Book();

        Document doc = Jsoup.parse(s);
        Element element = doc.selectFirst("div.alexwebdatainfogrid_name");
        if (element != null) {
            b.setTitle(element.html().trim());
        } else {
            return null;
        }

        element = doc.selectFirst("div.alexwebdatainfogrid_authoritem a");
        if (element != null) {
            b.setAuthor(element.html().trim());
        } else {
            return null;
        }

        element = doc.selectFirst("div.alexwebdatainfogrid_ean");
        if (element != null) {
            b.setISBN(element.html().trim());
        } else {
            return null;
        }

        return b;
    }

}
