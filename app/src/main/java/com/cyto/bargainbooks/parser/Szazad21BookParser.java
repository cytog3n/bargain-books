package com.cyto.bargainbooks.parser;

import android.content.Context;

import com.android.volley.toolbox.StringRequest;
import com.cyto.bargainbooks.config.Constants;
import com.cyto.bargainbooks.factory.Szazad21BookFactory;
import com.cyto.bargainbooks.model.Book;
import com.cyto.bargainbooks.request.handler.BookHandler;
import com.cyto.bargainbooks.request.handler.ErrorHandler;
import com.cyto.bargainbooks.service.VolleyService;

public class Szazad21BookParser {

    private VolleyService volleyService;

    private BookHandler bookHandler;

    private ErrorHandler errorHandler;

    public Szazad21BookParser(Context context, BookHandler bh, ErrorHandler eh) {
        this.volleyService = VolleyService.getInstance(context);
        this.bookHandler = bh;
        this.errorHandler = eh;
    }

    public void start(String url) {
        volleyService.addToRequestQueue(new StringRequest(url, response -> {
            Book b = Szazad21BookFactory.createBook(response);
            if (bookHandler != null) {
                bookHandler.handleBook(b);
            }
        }, error -> {
            Constants.errorListener.onErrorResponse(error);
            if (errorHandler != null) {
                errorHandler.handleError(error);
            }
        }).setRetryPolicy(Constants.requestPolicy));
    }
}
