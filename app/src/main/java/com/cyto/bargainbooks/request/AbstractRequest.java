package com.cyto.bargainbooks.request;

import android.util.Log;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.cyto.bargainbooks.model.Book;

public abstract class AbstractRequest {

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

    protected final Response.ErrorListener errorListener = error -> {
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            Log.e("TimeoutError | NoConnectionError", "Check your connection");
        } else if (error instanceof ServerError) {
            Log.e("NetworkError", "Server responded with an error response");
        } else if (error instanceof NetworkError) {
            Log.e("NetworkError", "There was a network error");
        } else if (error instanceof ParseError) {
            Log.e("ParseError", "Unable to parse the response");
        }
    };

    protected Long extractNumbers(String price) {
        return Long.valueOf(price.replaceAll("\\D+", ""));
    }

}
