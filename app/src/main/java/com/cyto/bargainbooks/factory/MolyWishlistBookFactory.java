package com.cyto.bargainbooks.factory;

import com.cyto.bargainbooks.model.Book;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MolyWishlistBookFactory {

    public static Book createBook(String s) {
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


        Elements elements = doc.select("div.items div[id^=edition]");
        if (elements != null) {
            element = elements.first(); // TODO ??

            String urlRegex = ": (\\d+)";
            Pattern p = Pattern.compile(urlRegex);
            Matcher m = p.matcher(element.html());
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
