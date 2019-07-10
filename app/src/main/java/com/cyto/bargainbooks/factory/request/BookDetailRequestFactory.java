package com.cyto.bargainbooks.factory.request;

import com.android.volley.toolbox.StringRequest;
import com.cyto.bargainbooks.config.Constants;
import com.cyto.bargainbooks.factory.book.BookFactory;
import com.cyto.bargainbooks.model.Book;
import com.cyto.bargainbooks.request.handler.BookHandler;
import com.cyto.bargainbooks.request.handler.ErrorHandler;

public class BookDetailRequestFactory {

    public StringRequest getStringRequest(String url, BookHandler bh, ErrorHandler eh, BookFactory bookFactory) {
        return new StringRequest(url, response -> {
            Book b = bookFactory.createBook(response);
            if (bh != null) {
                bh.handleBook(b);
            }
        }, error -> {
            Constants.errorListener.onErrorResponse(error);
            if (eh != null) {
                eh.handleError(error);
            }
        });
    }
}
