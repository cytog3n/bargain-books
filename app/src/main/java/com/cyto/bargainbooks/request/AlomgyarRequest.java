package com.cyto.bargainbooks.request;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cyto.bargainbooks.model.Book;
import com.cyto.bargainbooks.request.handler.BookHandler;
import com.cyto.bargainbooks.request.handler.ErrorHandler;
import com.cyto.bargainbooks.util.UrlUtil;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.Map;

public class AlomgyarRequest extends AbstractRequest {

    private Element detail;

    private final BookHandler bookHandler;

    private final ErrorHandler errorHandler;

    private Book book;

    public AlomgyarRequest(@NotNull Book book, @NotNull BookHandler bh, ErrorHandler eh) {
        this.book = book;
        this.bookHandler = bh;
        this.errorHandler = eh;
    }

    @Override
    public StringRequest getStringRequest() {

        Map<String, String> params = new HashMap<>();
        params.put("isbn", book.getISBN());
        String search = "https://alomgyar.hu/kereses?k=${isbn}";
        String url = UrlUtil.ApplyParameters(search, params);

        return new StringRequest(url, listener, failedRequestListener);
    }

    private final Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Document doc = Jsoup.parse(response);
            detail = doc.selectFirst("form.card.bookcard");

            if (detail != null && detail.selectFirst("input[name='elojegyez']") != null) {
                detail = null;
            }

            book = createBook(book.getISBN(), book.getAuthor(), book.getTitle());
            bookHandler.handleBook(book);
        }
    };

    private final Response.ErrorListener failedRequestListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            errorListener.onErrorResponse(error);
            if (errorHandler != null) {
                errorHandler.handleError(error);
            }
        }
    };

    @Override
    protected Long getBookOldPrice() {

        if (detail == null) {
            return -2L;
        }

        Element price = detail.selectFirst("div.ui.red.sub.header.strikethough");
        return price == null ? -1L : extractNumbers(price.html());
    }

    @Override
    protected Long getBookNewPrice() {

        if (detail == null) {
            return -2L;
        }

        Element price = detail.selectFirst("h2.ui.header.price");

        if (price == null) {
            return -1L;
        }

        price = price.clone();
        Element oldPrice = price.selectFirst("div.ui.red.sub.header.strikethough");

        if (oldPrice == null) {
            return -1L;
        } else {
            oldPrice.remove();
        }

        return extractNumbers(price.html());
    }

    @Override
    protected Long getSalePercent() {

        if (detail == null) {
            return -2L;
        }

        Long percent = Math.round((this.getBookNewPrice().doubleValue() / this.getBookOldPrice().doubleValue()) * 100);
        return 100 - percent;

    }

    @Override
    protected String getUrl() {
        if (detail != null) {
            Element url = detail.selectFirst("a");
            if (url != null) {
                return url.attr("href");
            }
        }
        return null;
    }

    @Override
    protected String getName() {
        return "alomgyar";
    }

}