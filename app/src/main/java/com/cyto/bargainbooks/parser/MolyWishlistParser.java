package com.cyto.bargainbooks.parser;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.cyto.bargainbooks.config.Constants;
import com.cyto.bargainbooks.factory.MolyWishlistBookFactory;
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

public class MolyWishlistParser {

    private VolleyService volleyService;

    private BookHandler bookHandler;

    private ErrorHandler errorHandler;

    private ListRequestHandler listHandler;

    private final String baseUrl = "https://moly.hu";

    public MolyWishlistParser(@NotNull Context context, ListRequestHandler listHandler, @NotNull BookHandler bh, ErrorHandler eh) {
        this.volleyService = VolleyService.getInstance(context);
        this.bookHandler = bh;
        this.errorHandler = eh;
        this.listHandler = listHandler;
    }

    public void start(String url) {
        volleyService.addToRequestQueue(new StringRequest(url, listResponse, error -> {
            Constants.errorListener.onErrorResponse(error);
            if (errorHandler != null) {
                errorHandler.handleError(error);
            }
        }).setRetryPolicy(Constants.requestPolicy));
    }

    private Response.Listener<String> listResponse = response -> {
        List<String> urls = new ArrayList<>();

        Document doc = Jsoup.parse(response);
        Elements list = doc.select("div.items div[id^=wish_]");

        for (Element elem : list) {
            Element a = elem.selectFirst("h3.item a");
            if (a != null) {
                urls.add(baseUrl + a.attr("href"));
            } else {
                // nop, just skip
            }
        }

        if (listHandler != null) {
            listHandler.handleRequest(urls.size());
        }

        for (String s : urls) {
            volleyService.addToRequestQueue(new StringRequest(s, response1 -> {
                bookHandler.handleBook(MolyWishlistBookFactory.createBook(response1));
            }, error -> {
                Constants.errorListener.onErrorResponse(error);
                if (errorHandler != null) {
                    errorHandler.handleError(error);
                }
            }).setRetryPolicy(Constants.requestPolicy));
        }

    };
}
