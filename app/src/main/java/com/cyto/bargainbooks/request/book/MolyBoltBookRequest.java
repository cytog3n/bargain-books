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

public class MolyBoltBookRequest extends AbstractBookRequest {

    private final BookHandler bookHandler;
    private final ErrorHandler errorHandler;
    private final Response.ErrorListener failedRequestListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Constants.errorListener.onErrorResponse(error);
            if (errorHandler != null) {
                errorHandler.handleError(error);
            }
        }
    };
    private Element detail;
    private Book book;
    private final Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Document doc = Jsoup.parse(response);
            detail = doc.selectFirst("table#products");

            book = createBook(book.getISBN(), book.getAuthor(), book.getTitle());
            bookHandler.handleBook(book);
        }
    };

    public MolyBoltBookRequest(@NotNull Book book, @NotNull BookHandler bh, ErrorHandler eh) {
        this.book = book;
        this.bookHandler = bh;
        this.errorHandler = eh;
    }

    @Override
    public StringRequest getStringRequest() {

        Map<String, String> params = new HashMap<>();
        params.put("isbn", book.getISBN());
        String search = "https://www.molybolt.hu/index.php?BODY=Search&OP=ISBNSearch&isbn=${isbn}";
        String url = UrlUtil.ApplyParameters(search, params);

        return new StringRequest(url, listener, failedRequestListener);
    }

    @Override
    protected Long getBookOldPrice() {

        if (detail == null) {
            return -2L;
        }

        Element price = detail.selectFirst("td.boltiartd nobr");
        return price == null ? -1L : extractNumbers(price.html());
    }

    @Override
    protected Long getBookNewPrice() {

        if (detail == null) {
            return -2L;
        }

        Element price = detail.selectFirst("td.internetesartd nobr");
        return price == null ? -1L : extractNumbers(price.html());
    }

    @Override
    protected Long getSalePercent() {

        if (detail == null) {
            return -2L;
        }

        long percent = Math.round((this.getBookNewPrice().doubleValue() / this.getBookOldPrice().doubleValue()) * 100);
        return 100 - percent;
    }

    @Override
    protected String getUrl() {
        if (detail != null) {
            Element url = detail.selectFirst("td a");
            if (url != null) {
                String base = "https://www.molybolt.hu/";
                return base + url.attr("href");
            }
        }
        return null;
    }

    @Override
    protected String getName() {
        return "molybolt";
    }

}
