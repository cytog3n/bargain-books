package com.cyto.bargainbooks.store.impl;

import android.content.Context;

import com.android.volley.toolbox.StringRequest;
import com.cyto.bargainbooks.factory.book.BookFactory;
import com.cyto.bargainbooks.factory.book.impl.KonyvudvarBookFactory;
import com.cyto.bargainbooks.factory.request.BookDetailRequestFactory;
import com.cyto.bargainbooks.model.Book;
import com.cyto.bargainbooks.request.book.KonyvudvarBookRequest;
import com.cyto.bargainbooks.request.handler.BookHandler;
import com.cyto.bargainbooks.request.handler.ErrorHandler;
import com.cyto.bargainbooks.request.handler.ListRequestHandler;
import com.cyto.bargainbooks.request.wishlist.WishlistRequest;
import com.cyto.bargainbooks.store.BookStore;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class KonyvudvarBookStore implements BookStore {

    BookDetailRequestFactory bdrf = new BookDetailRequestFactory();

    BookFactory bf = new KonyvudvarBookFactory();

    /**
     * @see BookStore#getStoreKey()
     */
    @Override
    public String getStoreKey() {
        return "konyvudvar";
    }

    /**
     * @see BookStore#getStoreName()
     */
    @Override
    public String getStoreName() {
        return "Könyvudvar";
    }

    /**
     * @see BookStore#getBookRequest(Book b, BookHandler bh, ErrorHandler eh)
     */
    @Override
    public StringRequest getBookRequest(Book b, BookHandler bh, ErrorHandler eh) {
        return new KonyvudvarBookRequest(b, bh, eh).getStringRequest();
    }

    /**
     * @see BookStore#matchBookDetail(String url)
     */
    @Override
    public boolean matchBookDetail(String url) {
        Pattern pattern = Pattern.compile("https:\\/\\/konyvudvar\\.net\\/.+");
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
        return false;
    }

    /**
     * @see BookStore#getWishListRequest(Context context, ListRequestHandler listHandler, BookHandler bh, ErrorHandler eh)
     */
    @Override
    public WishlistRequest getWishListRequest(Context context, ListRequestHandler listHandler, @NotNull BookHandler bh, ErrorHandler eh) {
        return null;
    }
}
