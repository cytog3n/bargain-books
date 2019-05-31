package com.cyto.bargainbooks.request;

import android.util.Log;

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
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SzalayKonyvekRequest extends  AbstractRequest {

    private Element detail;

    private final BookHandler bookHandler;

    private final ErrorHandler errorHandler;

    private Book book;

    public SzalayKonyvekRequest(@NotNull Book book, @NotNull BookHandler bh, ErrorHandler eh) {
        this.book = book;
        this.bookHandler = bh;
        this.errorHandler = eh;
    }

    @Override
    public StringRequest getStringRequest() {

        Map<String, String> params = new HashMap<>();
        params.put("isbn", book.getISBN());
        String search = "https://www.szalaykonyvek.hu/shop_search.php?search=${isbn}";
        String url = UrlUtil.ApplyParameters(search, params);

        return new StringRequest(url, listener, failedRequestListener);
    }

    private final Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Document doc = Jsoup.parse(response);
            Elements list = doc.select("form[name=form_temp_artdet]");
            if (list.size() == 0) {
                detail = null;
            } else {
                detail = list.first();
            }

            if (detail != null) {
                String urlRegex = "<link href=\"(.+)\" rel=\"canonical\" \\/>";
                Pattern p = Pattern.compile(urlRegex);
                Matcher m = p.matcher(response);
                if (m.find()) {
                    String url = m.group(1);
                    Log.d("url", m.group(1));
                }
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

        Element price = detail.selectFirst("span[id^=price_net_brutto]");
        return price == null ? -1L : extractNumbers(price.html());
    }

    @Override
    protected Long getBookNewPrice() {

        if (detail == null) {
            return -2L;
        }

        Element price = detail.selectFirst("span[id^=price_akcio_brutto]");
        return price == null ? -1L : extractNumbers(price.html());
    }

    @Override
    protected Long getSalePercent() {

        if (detail == null) {
            return -2L;
        }

        if (this.getBookNewPrice() > 0) {
            Long percent = Math.round((this.getBookNewPrice().doubleValue() / this.getBookOldPrice().doubleValue()) * 100);
            return 100 - percent;
        } else {
            return 0L;
        }

    }

    @Override
    protected String getUrl() {
        return detail == null ? null : detail.baseUri();
    }

    @Override
    protected String getName() {
        return "szalay";
    }
}
