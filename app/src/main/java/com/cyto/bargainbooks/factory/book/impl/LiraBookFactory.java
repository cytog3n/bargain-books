package com.cyto.bargainbooks.factory.book.impl;

import com.cyto.bargainbooks.factory.book.BookFactory;
import com.cyto.bargainbooks.model.Book;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * BookFactory for @url{https://www.lira.hu}
 *
 * @see BookFactory
 */
public class LiraBookFactory implements BookFactory {

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
            element = doc.selectFirst("div.title");
            if (element != null) {
                b.setTitle(element.html().trim());
            }
        }

        if (b.getTitle() == null) {
            return null;
        }

        element = doc.selectFirst("h2.author a");
        if (element != null) {
            b.setAuthor(element.html().trim());
        } else {
            element = doc.selectFirst("span.author");
            if (element != null) {
                b.setAuthor(element.html().trim());
            }
        }

        if (b.getAuthor() == null) {
            return null;
        }


        element = doc.selectFirst("div.product-details");

        if (element != null) {
            String urlRegex = "(\\d{13})";
            Pattern p = Pattern.compile(urlRegex);
            Matcher m = p.matcher(element.html());
            if (m.find()) {
                b.setISBN(m.group(1));
            }
        } else {
            element = doc.selectFirst("div b[id='cover_type']");
            if (element != null) {
                b.setISBN(element.html().trim());
            }
        }

        if (b.getISBN() == null) {
            return null;
        }

        return b;
    }

}
