package com.cyto.bargainbooks.factory.book.impl;

import com.cyto.bargainbooks.factory.book.BookFactory;
import com.cyto.bargainbooks.model.Book;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * BookFactory for @url{https://www.book24.hu}
 *
 * @see BookFactory
 */
public class Book24BookFactory implements BookFactory {

    /**
     * @see BookFactory#createBook(String)
     */
    @Override
    public Book createBook(String s) {
        Book b = new Book();

        Document doc = Jsoup.parse(s);
        Element element = doc.selectFirst("h1.title");
        if (element != null) {
            b.setTitle(element.html().trim());
        } else {
            return null;
        }

        element = doc.selectFirst("h2.author a span");
        if (element != null) {
            b.setAuthor(element.html().trim());
        } else {
            return null;
        }

        element = doc.selectFirst("span[itemprop='isbn']");
        if (element != null) {
            b.setISBN(element.html().trim());
        } else {
            return null;
        }

        return b;
    }

}
