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
 * BookFactory for @url{https://moly.hu}
 *
 * @see com.cyto.bargainbooks.factory.book.BookFactory
 */
public class MolyBookFactory implements BookFactory {

    /**
     * @see BookFactory#createBook(String)
     */
    @Override
    public Book createBook(String s) {
        Book b = new Book();

        Document doc = Jsoup.parse(s);
        Element element = doc.selectFirst("h1.book span.item span.fn");
        if (element != null) {
            for (Node child : element.childNodes()) {
                if (child instanceof TextNode) {
                    b.setTitle(((TextNode) child).text());
                    break;
                }
            }

            if (b.getTitle() == null) {
                return null;
            }
        } else {
            return null;
        }

        element = doc.selectFirst("div.authors a");
        if (element != null) {
            b.setAuthor(element.html());
        } else {
            return null;
        }


        Element elem = doc.selectFirst("div.items div[id^=edition]");
        if (elem != null) {

            String urlRegex = "<strong>ISBN<\\/strong>: (\\d+)";
            Pattern p = Pattern.compile(urlRegex);
            Matcher m = p.matcher(elem.html());
            if (m.find()) {
                b.setISBN(m.group(1));
            } else {
                return null;
            }
        } else {
            return null;
        }

        return b;
    }
}
