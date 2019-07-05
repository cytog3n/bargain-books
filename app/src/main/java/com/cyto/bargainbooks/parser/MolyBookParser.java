package com.cyto.bargainbooks.parser;

import android.content.Context;

import com.android.volley.toolbox.StringRequest;
import com.cyto.bargainbooks.config.Constants;
import com.cyto.bargainbooks.factory.MolyBookFactory;
import com.cyto.bargainbooks.model.Book;
import com.cyto.bargainbooks.request.handler.BookHandler;
import com.cyto.bargainbooks.request.handler.ErrorHandler;
import com.cyto.bargainbooks.service.VolleyService;

public class MolyBookParser {

    private VolleyService volleyService;

    private BookHandler bookHandler;

    private ErrorHandler errorHandler;

    public MolyBookParser(Context context, BookHandler bh, ErrorHandler eh) {
        this.volleyService = VolleyService.getInstance(context);
        this.bookHandler = bh;
        this.errorHandler = eh;
    }

    public void start(String url) {
        volleyService.addToRequestQueue(new StringRequest(url, response -> {
            Book b = MolyBookFactory.createBook(response);
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
