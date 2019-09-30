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
public class ScolarBookFactory implements BookFactory {

    /**
     * @see BookFactory#createBook(String)
     */
    @Override
    public Book createBook(String s) {
        Book b = new Book();

        Document doc = Jsoup.parse(s);
        Element element = doc.selectFirst("span.product-page-product-name");
        if (element != null) {
            b.setTitle(element.html().trim());
        } else {
            return null;
        }

        element = doc.selectFirst("span[itemprop='brand']");
        if (element != null) {
            b.setAuthor(element.html().trim());
        } else {
            return null;
        }

        element = doc.selectFirst("td.param-value.manufacturersku-param");
        if (element != null) {
            b.setISBN(element.html().trim());
        } else {
            return null;
        }

        return b;
    }

}
