package com.cyto.bargainbooks.factory.book.impl;

import com.cyto.bargainbooks.factory.book.BookFactory;
import com.cyto.bargainbooks.model.Book;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * BookFactory for @url{https://alomgyar.hu}
 *
 * @see BookFactory
 */
public class AlomgyarBookFactory implements BookFactory {

    /**
     * @see BookFactory#createBook(String)
     */
    @Override
    public Book createBook(String s) {
        Book b = new Book();

        Document doc = Jsoup.parse(s);
        Element element = doc.selectFirst("div.ui.large.header");

        for (Node node : element.childNodes()) {
            if (node instanceof TextNode) {
                b.setTitle(((TextNode) node).text().trim());
                break;
            }
        }

        if (b.getTitle() == null) {
            return null;
        }

        element = doc.selectFirst("h3.ui.sub.header a.ui");
        if (element != null) {
            b.setAuthor(element.html().trim());
        } else {
            return null;
        }

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
