package com.cyto.bargainbooks.factory.book.impl;

import com.cyto.bargainbooks.factory.book.BookFactory;
import com.cyto.bargainbooks.model.Book;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * BookFactory for @url{https://konyvudvar.net}
 *
 * @see BookFactory
 */
public class KonyvudvarBookFactory implements BookFactory {

    /**
     * @see BookFactory#createBook(String)
     */
    @Override
    public Book createBook(String s) {
        Book b = new Book();

        Document doc = Jsoup.parse(s);
        Element element = doc.selectFirst("h1.heading-title");
        if (element != null) {
            b.setTitle(element.html().trim());
        } else {
            return null;
        }

        element = doc.selectFirst("div.tags a");
        if (element != null) {
            b.setAuthor(element.html().trim());
        } else {
            return null;
        }

        element = doc.selectFirst("table.attribute");
        String urlRegex = "(\\d{13})";
        Pattern p = Pattern.compile(urlRegex);
        Matcher m = p.matcher(element.html());
        if (m.find()) {
            b.setISBN(m.group(1));
        } else {
            return null;
        }

        return b;
    }

}
