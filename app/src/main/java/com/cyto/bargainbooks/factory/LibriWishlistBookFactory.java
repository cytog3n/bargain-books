package com.cyto.bargainbooks.factory;

import com.cyto.bargainbooks.model.Book;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LibriWishlistBookFactory {

    private static Element element;

    public static Book createBook(String s) {
        Book b = new Book();

        Document doc = Jsoup.parse(s);
        element = doc.selectFirst("span.product-title");
        if (element != null) {
            b.setTitle(element.html());
        } else {
            return null;
        }

        element = doc.selectFirst("h2.authors a");
        if (element != null) {
            b.setAuthor(element.html());
        } else {
            return null;
        }

        // <meta itemprop="isbn" content="9789632638164">
        String urlRegex = "<meta itemprop=\"isbn\" content=\"(.+)\">";
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