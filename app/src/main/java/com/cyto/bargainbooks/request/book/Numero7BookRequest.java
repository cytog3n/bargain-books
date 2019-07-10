package com.cyto.bargainbooks.request.book;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cyto.bargainbooks.config.Constants;
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

public class Numero7BookRequest extends AbstractBookRequest {

    private Element detail;

    private final BookHandler bookHandler;

    private final ErrorHandler errorHandler;

    private Book book;

    public Numero7BookRequest(@NotNull Book book, @NotNull BookHandler bh, ErrorHandler eh) {
        this.book = book;
        this.bookHandler = bh;
        this.errorHandler = eh;
    }

    @Override
    public StringRequest getStringRequest() {

        Map<String, String> params = new HashMap<>();
        params.put("isbn", book.getISBN());
        String search = "https://www.numero7.com/search_responsive.pl?s=${isbn}";
        String url = UrlUtil.ApplyParameters(search, params);

        return new StringRequest(url, listener, failedRequestListener);
    }

    private final Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Document doc = Jsoup.parse(response);
            detail = doc.selectFirst("div.item");

            book = createBook(book.getISBN(), book.getAuthor(), book.getTitle());
            bookHandler.handleBook(book);
        }
    };

    private final Response.ErrorListener failedRequestListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Constants.errorListener.onErrorResponse(error);
            if (errorHandler != null) {
                errorHandler.handleError(error);
            }
        }
    };

    // TODO
    @Override
    protected Long getBookOldPrice() {

        /*
         * if (detail == null) { return -2L; }
         *
         * Element price = detail.selectFirst("span[itemprop=price]"); return price == null ? -1L : extractNumbers(price.html());
         */
        return -1L;
    }

    @Override
    protected Long getBookNewPrice() {

        if (detail == null) {
            return -2L;
        }

        Element price = detail.selectFirst("span[itemprop=price]");
        return price == null ? -1L : extractNumbers(price.html());
    }

    @Override
    protected Long getSalePercent() {

        if (detail == null) {
            return -2L;
        }

        Element perc = detail.selectFirst("span.label.label-primary");
        return perc == null ? -1L : extractNumbers(perc.html());
    }

    @Override
    protected String getUrl() {
        if (detail != null) {
            Element url = detail.selectFirst("a[itemprop=url]");
            if (url != null) {
                String base = "https://www.numero7.com";
                return base + url.attr("href");
            }
        }
        return null;
    }

    @Override
    protected String getName() {
        return "numero7";
    }
}
