package com.cyto.bargainbooks.request.book;

import com.android.volley.toolbox.StringRequest;
import com.cyto.bargainbooks.model.Book;

public abstract class AbstractBookRequest {

    protected abstract String getName();

    protected abstract Long getBookOldPrice();

    protected abstract Long getBookNewPrice();

    protected abstract Long getSalePercent();

    protected abstract String getUrl();

    protected Book createBook(String ISBN, String author, String title) {

        Book b = new Book();
        b.setISBN(ISBN);
        b.setNewPrice(getBookNewPrice());
        b.setOriginalPrice(getBookOldPrice());
        b.setStore(getName());
        b.setUrl(getUrl());
        b.setAuthor(author);
        b.setTitle(title);
        b.setSalePercent(getSalePercent());
        return b;

    }

    public abstract StringRequest getStringRequest();

    protected Long extractNumbers(String price) {
        return Long.valueOf(price.replaceAll("\\D+", ""));
    }

}
