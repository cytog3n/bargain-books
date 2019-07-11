package com.cyto.bargainbooks.store;

import android.content.Context;

import com.android.volley.toolbox.StringRequest;
import com.cyto.bargainbooks.model.Book;
import com.cyto.bargainbooks.request.book.AbstractBookRequest;
import com.cyto.bargainbooks.request.handler.BookHandler;
import com.cyto.bargainbooks.request.handler.ErrorHandler;
import com.cyto.bargainbooks.request.handler.ListRequestHandler;
import com.cyto.bargainbooks.request.wishlist.WishlistRequest;

import org.jetbrains.annotations.NotNull;

/**
 * This interface contains all the information about a store.
 * <br>
 * For extending the application, implement this, and add to the {@link com.cyto.bargainbooks.config.BookStoreList} at initialization
 */
public interface BookStore {

    /**
     * @return This will be the key of the store. Usually a short but clear ID
     */
    String getStoreKey();

    /**
     * @return This will be the name of the store. This name will be shown in the application
     */
    String getStoreName();

    /**
     * Returns a {@link StringRequest} with a correct {@link com.android.volley.Response.Listener} which calls back with the given {@link BookHandler} <br>
     * Used in {@link com.cyto.bargainbooks.fragment.SaleFragment} and by {@link com.cyto.bargainbooks.factory.request.BookRequestFactory} <br>
     * The extension if {@link AbstractBookRequest} is highly recommended
     *
     * @param b  empty {@link Book} with Title, Author, ISBN fields
     * @param bh callback
     * @param eh callback
     * @return StringRequest with filled {@link com.android.volley.Response.Listener}
     */
    StringRequest getBookRequest(Book b, BookHandler bh, ErrorHandler eh);

    /**
     * Checks if the Store is able to handle the URL for adding a book. <br>
     * Used in {@link com.cyto.bargainbooks.fragment.AddBookFragment}
     *
     * @param url URL of the book detail page
     * @return true if able to handle, false if not. If the {@link #getBookDetailRequest(String, BookHandler, ErrorHandler)} returns null, ALWAYS return false
     */
    boolean matchBookDetail(String url);

    /**
     * Returns a {@link StringRequest} with a correct {@link com.android.volley.Response.Listener} which calls back with the given {@link BookHandler} <br>
     * Used in {@link com.cyto.bargainbooks.fragment.AddBookFragment}
     *
     * @param url URL of the book detail
     * @param bh  callback
     * @param eh  callback
     * @return {@link com.android.volley.toolbox.StringRequest} with filled {@link com.android.volley.Response.Listener}
     */
    StringRequest getBookDetailRequest(String url, BookHandler bh, ErrorHandler eh);

    /**
     * Checks if the Store is able to handle the URL for importing a wishlist. <br>
     * Used in {@link com.cyto.bargainbooks.fragment.ImportOnlineWishlistFragment}
     *
     * @param url URL of the wishlist
     * @return true if able to handle, false if not. If the {@link #getWishListRequest(Context, ListRequestHandler, BookHandler, ErrorHandler)} returns null, ALWAYS return false
     */
    boolean matchWishlistUrl(String url);

    /**
     * Returns a {@link WishlistRequest} which can be called for starting the query.
     *
     * @param context     Context of the application (Used by {@link com.cyto.bargainbooks.service.VolleyService} )
     * @param listHandler callback
     * @param bh          callback
     * @param eh          callback
     * @return
     */
    WishlistRequest getWishListRequest(Context context, ListRequestHandler listHandler, @NotNull BookHandler bh, ErrorHandler eh);

}

