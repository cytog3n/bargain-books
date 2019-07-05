package com.cyto.bargainbooks.factory;

import com.cyto.bargainbooks.model.Book;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BooklineBookFactory {

    public static Book createBook(String s) {
        Book b = new Book();

        Document doc = Jsoup.parse(s);
        Element element = doc.selectFirst("h1.c-product__title");
        if (element != null) {
            b.setTitle(element.html());
        } else {
            return null;
        }

        element = doc.selectFirst("a.c-product__author");
        if (element != null) {
            b.setAuthor(element.html());
        } else {
            return null;
        }

        // 299 oldal･kemény kötés･ISBN: 9789638691361
        String urlRegex = "ISBN: (\\d+)";
        Pattern p = Pattern.compile(urlRegex);
        Matcher m = p.matcher(s);
        if (m.find()) {
            b.setISBN(m.group(1));
        } else {
            return null;
        }

        return b;
    }

}
