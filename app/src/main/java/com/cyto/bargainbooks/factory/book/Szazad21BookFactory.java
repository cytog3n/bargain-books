package com.cyto.bargainbooks.factory.book;

import com.cyto.bargainbooks.model.Book;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Szazad21BookFactory implements BookFactory {

    @Override
    public Book createBook(String s) {
        Book b = new Book();

        Document doc = Jsoup.parse(s);
        Element element = doc.selectFirst("span[itemprop=\"name\"]");
        if (element != null) {
            b.setTitle(element.html());
        } else {
            return null;
        }

        element = doc.selectFirst("tr.featured-param-1");
        if (element != null) {
            if (element.child(1) != null) {
                b.setAuthor(element.child(1).html());
            } else {
                return null;
            }
        } else {
            return null;
        }

        if (b.getTitle().contains("-")) {
            b.setTitle(b.getTitle().substring(0, b.getTitle().lastIndexOf("-")).trim()); // the page puts the author in the title... if there is '-' in the title... the title must be edited... sorry
        }

        element = doc.selectFirst("span[itemprop=\"sku\"]");
        if (element != null) {
            b.setISBN(element.html());
        } else {
            return null;
        }

        return b;
    }

}
