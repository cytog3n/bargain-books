package com.cyto.bargainbooks.factory.book.impl;

import com.cyto.bargainbooks.factory.book.BookFactory;
import com.cyto.bargainbooks.model.Book;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * BookFactory for @url{https://www.mai-konyv.hu}
 *
 * @see BookFactory
 */
public class MaiKonyvBookFactory implements BookFactory {

    /**
     * @see BookFactory#createBook(String)
     */
    @Override
    public Book createBook(String s) {
        Book b = new Book();

        Document doc = Jsoup.parse(s);
        Element element = doc.selectFirst("span.product-page-product-name");
        if (element != null) {
            b.setTitle(element.html().replaceAll("\\(.*\\)", "").trim());
        } else {
            return null;
        }

        element = doc.selectFirst("span[itemprop='brand']");
        if (element != null) {
            b.setAuthor(element.html().trim());
        } else {
            return null;
        }

        element = doc.selectFirst("table.parameter_table");
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
