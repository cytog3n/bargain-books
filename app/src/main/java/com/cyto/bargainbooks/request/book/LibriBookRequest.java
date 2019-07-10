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

public class LibriBookRequest extends AbstractBookRequest {

    private Element detail;

    private final BookHandler bookHandler;

    private final ErrorHandler errorHandler;

    private Book book;

    public LibriBookRequest(@NotNull Book book, @NotNull BookHandler bh, ErrorHandler eh) {
        this.book = book;
        this.bookHandler = bh;
        this.errorHandler = eh;
    }

    @Override
    public StringRequest getStringRequest() {

        Map<String, String> params = new HashMap<>();
        params.put("isbn", book.getISBN());
        String search = "https://www.libri.hu/talalati_lista/?reszletes=0&s_det=1&szerzo=&cim=&keyword=&s_cim=&book_target_id=&book_era_id=&book_location_id=&book_lang_id=&kiado=&isbn=${isbn}&ar1=&ar2=&ev1=&ev2=&ext_id=&transport_id=";
        String url = UrlUtil.ApplyParameters(search, params);

        return new StringRequest(url, listener, failedRequestListener);
    }

    private final Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Document doc = Jsoup.parse(response);
            detail = doc.selectFirst("article.box-product-full");

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

    @Override
    protected Long getBookOldPrice() {

        if (detail == null) {
            return -2L;
        }

        Element price = detail.selectFirst("del");
        return price == null ? -1L : extractNumbers(price.html());
    }

    @Override
    protected Long getBookNewPrice() {

        if (detail == null) {
            return -2L;
        }

        Element price = detail.selectFirst("span.act-price");
        if (getBookOldPrice() == -1) {
            return -1L;
        }
        return price == null ? -1L : extractNumbers(price.html());
    }

    @Override
    protected Long getSalePercent() {

        if (detail == null) {
            return -2L;
        }

        Element perc = detail.selectFirst("span.list-percent");
        return perc == null ? -1L : extractNumbers(perc.html());
    }

    @Override
    protected String getUrl() {
        if (detail != null) {
            Element url = detail.selectFirst("a");
            if (url != null) {
                String base = "https://www.libri.hu";
                return base + url.attr("href");
            }
        }
        return null;
    }

    @Override
    protected String getName() {
        return "libri";
    }
}
