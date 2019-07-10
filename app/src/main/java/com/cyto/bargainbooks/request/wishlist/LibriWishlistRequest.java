package com.cyto.bargainbooks.request.wishlist;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.cyto.bargainbooks.config.Constants;
import com.cyto.bargainbooks.factory.book.BookFactory;
import com.cyto.bargainbooks.factory.book.LibriBookFactory;
import com.cyto.bargainbooks.request.handler.BookHandler;
import com.cyto.bargainbooks.request.handler.ErrorHandler;
import com.cyto.bargainbooks.request.handler.ListRequestHandler;
import com.cyto.bargainbooks.service.VolleyService;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * This class will handle the Libri wishlists.
 */
public class LibriWishlistRequest extends AbstractWishlistRequest {

    private final String baseUrl = "https://www.libri.hu";

    private final BookFactory bookFactory = new LibriBookFactory();

    /**
     * The {@link com.android.volley.Response.Listener} for the first call.
     */
    private Response.Listener<String> listResponse = response -> {
        List<String> urls = new ArrayList<>();

        Document doc = Jsoup.parse(response);
        Elements list = doc.select("div.box-product-basket-list");

        for (Element elem : list) {
            Element a = elem.selectFirst("a.title");
            if (a != null) {
                urls.add(baseUrl + a.attr("href"));
            }
        }

        if (listHandler != null) {
            listHandler.handleRequest(urls.size());
        }

        for (String s : urls) {
            volleyService.addToRequestQueue(new StringRequest(s, response1 -> {
                bookHandler.handleBook(bookFactory.createBook(response));
            }, error -> {
                Constants.errorListener.onErrorResponse(error);
                if (errorHandler != null) {
                    errorHandler.handleError(error);
                }
            }).setRetryPolicy(Constants.requestPolicy));
        }

    };

    /**
     * Creates a new instance of LibriWishlistRequest
     *
     * @param context     Will be used for the {@link VolleyService}
     * @param listHandler Will be used for informing the users about the size of the list
     * @param bh          Will handle the {@link com.cyto.bargainbooks.model.Book} entities
     * @param eh          Will handle the exceptions. Optional
     */
    public LibriWishlistRequest(@NotNull Context context, ListRequestHandler listHandler, @NotNull BookHandler bh, ErrorHandler eh) {
        this.volleyService = VolleyService.getInstance(context);
        this.bookHandler = bh;
        this.errorHandler = eh;
        this.listHandler = listHandler;
    }

    /**
     * @see AbstractWishlistRequest#start(String)
     */
    @Override
    public void start(String url) {
        volleyService.addToRequestQueue(new StringRequest(url, listResponse, error -> {
            Constants.errorListener.onErrorResponse(error);
            if (errorHandler != null) {
                errorHandler.handleError(error);
            }
        }).setRetryPolicy(Constants.requestPolicy));
    }

}
