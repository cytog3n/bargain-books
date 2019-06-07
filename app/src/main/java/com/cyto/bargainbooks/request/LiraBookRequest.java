package com.cyto.bargainbooks.request;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LiraBookRequest extends AbstractBookRequest {

    private String url;

    private Element detail;

    private final BookHandler bookHandler;

    private final ErrorHandler errorHandler;

    private Book book;

    public LiraBookRequest(@NotNull Book book, @NotNull BookHandler bh, ErrorHandler eh) {
        this.book = book;
        this.bookHandler = bh;
        this.errorHandler = eh;
    }

    @Override
    public StringRequest getStringRequest() {

        Map<String, String> params = new HashMap<>();
        params.put("isbn", book.getISBN());
        String search = "https://www.lira.hu/hu/reszletes_kereso?listtype=1&listorder=release_date&listdirection=desc&listpagenumber=20&listcurrentpage=0&detaled_search_category=001&detaled_search_title=&detaled_search_isbn=${isbn}&detaled_search_year=&detaled_search_price1=&detaled_search_price2=&detaled_search_author=&detaled_search_publisher=&detaled_search_description=&detaled_search_series=&detaled_search_labels=";
        String url = UrlUtil.ApplyParameters(search, params);

        return new StringRequest(url, listener, failedRequestListener);
    }

    private final Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Document doc = Jsoup.parse(response);
            detail = doc.selectFirst("div.detail_item");

            if (detail != null) {
                String urlRegex = "<link rel=\"canonical\" href=\"(.+)\" \\/>";
                Pattern p = Pattern.compile(urlRegex);
                Matcher m = p.matcher(response);
                if (m.find()) {
                    url = m.group(1);
                }
            }

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

        Element price = detail.selectFirst("div.price.text-left");
        return price == null ? -1L : extractNumbers(price.html());
    }

    @Override
    protected Long getBookNewPrice() {

        if (detail == null) {
            return -2L;
        }

        Element price = detail.selectFirst("div.discounted_price.text-left");
        return price == null ? -1L : extractNumbers(price.html());
    }

    @Override
    protected Long getSalePercent() {

        if (detail == null) {
            return -2L;
        }

        Element perc = detail.selectFirst("div.percent.text-left");
        return perc == null ? -1L : extractNumbers(perc.html());
    }

    @Override
    protected String getUrl() {
        return detail == null ? null : url;
    }

    @Override
    protected String getName() {
        return "lira";
    }

}
