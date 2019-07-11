package com.cyto.bargainbooks.request.handler;

import com.cyto.bargainbooks.model.Book;

/**
 * This interface is used as a callback in the {@link com.android.volley.Response.Listener}
 */
public interface BookHandler {

    /**
     * Callback with a {@link Book}
     *
     * @param b
     */
    void handleBook(Book b);

}
