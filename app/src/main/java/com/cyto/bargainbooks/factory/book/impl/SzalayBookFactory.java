package com.cyto.bargainbooks.factory.book.impl;

import com.cyto.bargainbooks.factory.book.BookFactory;
import com.cyto.bargainbooks.model.Book;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

/**
 * BookFactory for @url{https://alexandra.hu}
 *
 * @see BookFactory
 */
public class SzalayBookFactory implements BookFactory {

    /**
     * @see BookFactory#createBook(String)
     */
    @Override
    public Book createBook(String s) {
        Book b = new Book();

        Document doc = Jsoup.parse(s);
        Element element = doc.selectFirst("div.page_artdet_2_name h1");
        if (element != null) {
            b.setTitle(element.html().trim());
        } else {
            return null;
        }

        element = doc.selectFirst("#tab_description_content");
        if (element != null) {

            element = element.selectFirst("div.book_info span");
            for (Node node : element.childNodes()) {
                if (node instanceof TextNode) {
                    b.setAuthor(((TextNode) node).text().trim());
                }
            }
        } else {
            return null;
        }

        element = doc.selectFirst("div.page_artdet_param_value");
        if (element != null) {
            b.setISBN(element.html().replace(" ", "").trim());
        } else {
            return null;
        }

        return b;
    }

}
