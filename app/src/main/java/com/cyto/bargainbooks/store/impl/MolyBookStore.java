package com.cyto.bargainbooks.store.impl;

import android.content.Context;

import com.android.volley.toolbox.StringRequest;
import com.cyto.bargainbooks.factory.book.BookFactory;
import com.cyto.bargainbooks.factory.book.impl.MolyBookFactory;
import com.cyto.bargainbooks.factory.request.BookDetailRequestFactory;
import com.cyto.bargainbooks.model.Book;
import com.cyto.bargainbooks.request.book.MolyBoltBookRequest;
import com.cyto.bargainbooks.request.handler.BookHandler;
import com.cyto.bargainbooks.request.handler.ErrorHandler;
import com.cyto.bargainbooks.request.handler.ListRequestHandler;
import com.cyto.bargainbooks.request.wishlist.WishlistRequest;
import com.cyto.bargainbooks.request.wishlist.impl.MolyWishlistRequest;
import com.cyto.bargainbooks.store.BookStore;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class MolyBookStore implements BookStore {

    private BookFactory bf = new MolyBookFactory();

    private BookDetailRequestFactory bdrf = new BookDetailRequestFactory();

    /**
     * @see BookStore#getStoreKey()
     */
    @Override
    public String getStoreKey() {
        return "moly";
    }

    /**
     * @see BookStore#getStoreName()
     */
    @Override
    public String getStoreName() {
        return "Molybolt";
    }

    /**
     * @see BookStore#getBookRequest(Book b, BookHandler bh, ErrorHandler eh)
     */
    @Override
    public StringRequest getBookRequest(Book b, BookHandler bh, ErrorHandler eh) {
        return new MolyBoltBookRequest(b, bh, eh).getStringRequest();
    }

    /**
     * @see BookStore#matchBookDetail(String url)
     */
    @Override
    public boolean matchBookDetail(String url) {
        Pattern pattern = Pattern.compile("https:\\/\\/moly\\.hu\\/konyvek\\/.+");
        return pattern.matcher(url).find();
    }

    /**
     * @see BookStore#getBookDetailRequest(String url, BookHandler bh, ErrorHandler eh)
     */
    @Override
    public StringRequest getBookDetailRequest(String url, BookHandler bh, ErrorHandler eh) {
        return bdrf.getStringRequest(url, bh, eh, bf);
    }

    /**
     * @see BookStore#matchWishlistUrl(String url)
     */
    @Override
    public boolean matchWishlistUrl(String url) {
        Pattern pattern = Pattern.compile("https:\\/\\/moly\\.hu\\/tagok\\/.+\\/kivansaglista");
        return pattern.matcher(url).find();
    }

    /**
     * @see BookStore#getWishListRequest(Context context, ListRequestHandler listHandler, BookHandler bh, ErrorHandler eh)
     */
    @Override
    public WishlistRequest getWishListRequest(Context context, ListRequestHandler listHandler, @NotNull BookHandler bh, ErrorHandler eh) {
        return new MolyWishlistRequest(context, listHandler, bh, eh);
    }
}
