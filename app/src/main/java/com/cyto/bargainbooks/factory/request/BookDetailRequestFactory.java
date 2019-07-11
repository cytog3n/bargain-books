package com.cyto.bargainbooks.factory.request;

import com.android.volley.toolbox.StringRequest;
import com.cyto.bargainbooks.config.Constants;
import com.cyto.bargainbooks.factory.book.BookFactory;
import com.cyto.bargainbooks.model.Book;
import com.cyto.bargainbooks.request.handler.BookHandler;
import com.cyto.bargainbooks.request.handler.ErrorHandler;

/**
 * This class is used for {@link com.cyto.bargainbooks.config.BookStoreList.BookStore}
 */
public class BookDetailRequestFactory {

    /**
     * Returns a {@link StringRequest} with populated fields
     *
     * @param url         URL of the BookDetail
     * @param bh          callback
     * @param eh          callback
     * @param bookFactory the {@link BookFactory} which can handle the HTML on the given URL
     * @return
     */
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
