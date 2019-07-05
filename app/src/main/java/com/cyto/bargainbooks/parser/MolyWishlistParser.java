package com.cyto.bargainbooks.parser;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.cyto.bargainbooks.config.Constants;
import com.cyto.bargainbooks.factory.MolyBookFactory;
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

    private final List<String> pageUrls = new ArrayList<>();

    private final List<String> bookUrls = new ArrayList<>();

    private String url = null;

    private Long pageCount;

    private Long pageResponseCount;

    public MolyWishlistParser(@NotNull Context context, ListRequestHandler listHandler, @NotNull BookHandler bh, ErrorHandler eh) {
        this.volleyService = VolleyService.getInstance(context);
        this.bookHandler = bh;
        this.errorHandler = eh;
        this.listHandler = listHandler;
    }

    public void start(String url) {
        this.url = url;
        pageResponseCount = 1L; // Since the first query pulls the first 10 books, it's technicallly the first page
        pageCount = 0L;
        pageUrls.clear();
        bookUrls.clear();
        volleyService.addToRequestQueue(new StringRequest(url, onResponse, error -> {
            Constants.errorListener.onErrorResponse(error);
            if (errorHandler != null) {
                errorHandler.handleError(error);
            }
        }).setRetryPolicy(Constants.requestPolicy));
    }

    private Response.Listener<String> onResponse = response -> {

        Document doc = Jsoup.parse(response);
        addBooks(doc);

        Element element = doc.selectFirst("div.pagination");
        if (element != null) {
            Elements elements = element.select("a[href*=page]").select("a:not(.next_page)");
            if (elements.size() > 0) {
                pageCount = Long.parseLong(elements.last().html());
            }
            for (int i = 2; i <= pageCount; i++) {
                pageUrls.add(url + "?page=" + i);
            }
        } else {
            queryBooks();
        }

        // If there is no pagination, this part will be ignored
        for (String s : pageUrls) {
            volleyService.addToRequestQueue(new StringRequest(s, response1 -> {
                Document doc1 = Jsoup.parse(response1);
                addBooks(doc1);

                pageResponseCount++;
                if (pageResponseCount == pageCount) {
                    queryBooks();
                }
            }, error -> {
                Constants.errorListener.onErrorResponse(error);
                if (errorHandler != null) {
                    errorHandler.handleError(error);
                }
            }).setRetryPolicy(Constants.requestPolicy));
        }
    };

    private void addBooks(Document doc) {

        Elements list = doc.select("div.items div[id^=wish_]");

        for (Element elem : list) {
            Element a = elem.selectFirst("h3.item a");
            if (a != null) {
                bookUrls.add(baseUrl + a.attr("href"));
            } else {
                // nop, just skip
            }
        }
    }

    private void queryBooks() {
        if (listHandler != null) {
            listHandler.handleRequest(bookUrls.size());
        }

        for (String s : bookUrls) {
            volleyService.addToRequestQueue(new StringRequest(s, response -> {
                bookHandler.handleBook(MolyBookFactory.createBook(response));
            }, error -> {
                Constants.errorListener.onErrorResponse(error);
                if (errorHandler != null) {
                    errorHandler.handleError(error);
                }
            }).setRetryPolicy(Constants.requestPolicy));
        }
    }
}
