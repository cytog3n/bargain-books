package com.cyto.bargainbooks.request.wishlist;

import com.cyto.bargainbooks.request.handler.BookHandler;
import com.cyto.bargainbooks.request.handler.ErrorHandler;
import com.cyto.bargainbooks.request.handler.ListRequestHandler;
import com.cyto.bargainbooks.service.VolleyService;

/**
 * The childs of this class will handle the ListRequests (the online and offline lists as well)
 */
public abstract class AbstractWishlistRequest {

    protected VolleyService volleyService;

    protected BookHandler bookHandler;

    protected ErrorHandler errorHandler;

    protected ListRequestHandler listHandler;

    /**
     * This method will start a query, and call the bookHandler with the results.
     *
     * @param url The page of the URL or the URI for the offline file
     * @see BookHandler
     */
    public abstract void start(String url);

}
