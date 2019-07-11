package com.cyto.bargainbooks.factory.request;

import android.content.Context;

import com.android.volley.toolbox.StringRequest;
import com.cyto.bargainbooks.config.BookStoreList;
import com.cyto.bargainbooks.model.Book;
import com.cyto.bargainbooks.request.handler.BookHandler;
import com.cyto.bargainbooks.request.handler.ErrorHandler;
import com.cyto.bargainbooks.storage.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * This class creates filtered {@link StringRequest} entities
 */
public class BookRequestFactory {

    private final Context context;

    /**
     * Creates a new {@link BookRequestFactory}
     *
     * @param context used for getting the {@link Config} for the application
     */
    public BookRequestFactory(Context context) {
        this.context = context;
    }

    /**
     * Returns {@link StringRequest} entities for each registered store (which are not turned off via filter)
     * <br>
     *
     * @param b  empty {@link Book} entity which only contains Author, Title, ISBN
     * @param bh callback
     * @param eh callback
     * @return list of the filtered {@link StringRequest}
     */
    public List<StringRequest> getRequests(Book b, BookHandler bh, ErrorHandler eh) {
        List<StringRequest> requests = new ArrayList<>();
        Config c = Config.getInstance(context);

        for (String storeName : BookStoreList.getStoreKeys()) {
            Boolean filterOn = c.getStoreFilter().get(storeName);
            if (filterOn) {
                requests.add(BookStoreList.getBookStoreByKey(storeName).getBookRequest(b, bh, eh));
            }
        }

        return requests;
    }

}
