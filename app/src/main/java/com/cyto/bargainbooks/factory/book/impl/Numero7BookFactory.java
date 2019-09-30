package com.cyto.bargainbooks.factory.book.impl;

import com.cyto.bargainbooks.factory.book.BookFactory;
import com.cyto.bargainbooks.model.Book;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * BookFactory for @url{https://www.numero7.com}
 *
 * @see BookFactory
 */
public class Numero7BookFactory implements BookFactory {

    /**
     * @see BookFactory#createBook(String)
     */
    @Override
    public Book createBook(String s) {
        Book b = new Book();

        Document doc = Jsoup.parse(s);
        Element element = doc.selectFirst("h4[itemprop='name']");
        if (element != null) {
            b.setTitle(capitalize(element.html().trim()));
        } else {
            return null;
        }

        element = doc.selectFirst("h5[itemprop='author']");
        if (element != null) {
            b.setAuthor(getAuthor(element.html().trim()));
        } else {
            return null;
        }

        element = doc.selectFirst("td[itemprop='isbn']");
        if (element != null) {
            b.setISBN(element.html().trim());
        } else {
            return null;
        }

        return b;
    }


    private String getAuthor(String s) {

        // ex. CONNELLY, MICHAEL
        String[] array = s.split(",");

        if (array.length == 2) {
            return capitalize(array[1]) + " " + capitalize(array[1]);
        } else {
            StringBuilder sb = new StringBuilder();
            for (String part : array) {
                sb.append(capitalize(part) + " ");
            }
            return sb.toString().trim();
        }
    }

    private String capitalize(String s) {
        if (s != null && s.length() > 0) {
            return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
        } else {
            return s;
        }

    }

}
