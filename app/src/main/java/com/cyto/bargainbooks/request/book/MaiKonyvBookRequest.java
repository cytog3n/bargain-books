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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MaiKonyvBookRequest extends AbstractBookRequest {

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
    private String url = null;
    private Book book;
    private final Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Document doc = Jsoup.parse(response);
            detail = doc.selectFirst("div.row.product-content-column-left");

            if (detail != null) {
                String urlRegex = "<meta property=\"og:url\" content=\"(.+)\" \\/>";
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

    public MaiKonyvBookRequest(@NotNull Book book, @NotNull BookHandler bh, ErrorHandler eh) {
        this.book = book;
        this.bookHandler = bh;
        this.errorHandler = eh;
    }

    @Override
    public StringRequest getStringRequest() {

        Map<String, String> params = new HashMap<>();
        params.put("isbn", book.getISBN());
        String search = "https://www.mai-konyv.hu/index.php?route=product/list&keyword=${isbn}&category_id=57";
        String url = UrlUtil.ApplyParameters(search, params);

        return new StringRequest(url, listener, failedRequestListener);
    }

    @Override
    protected Long getBookOldPrice() {

        if (detail == null) {
            return -2L;
        }

        if (detail.selectFirst("a.notify-request") != null) { // Available or not
            return -1L;
        }

        Element price = detail.selectFirst("span.price.price_original_color.product_table_original");
        return price == null ? -1L : extractNumbers(price.html());
    }

    @Override
    protected Long getBookNewPrice() {

        if (detail == null) {
            return -2L;
        }

        if (detail.selectFirst("a.notify-request") != null) { // Available or not
            return -1L;
        }

        Element price = detail.selectFirst("span.price.price_special_color.product_table_special");
        return price == null ? -1L : extractNumbers(price.html());
    }

    @Override
    protected Long getSalePercent() {

        if (detail == null) {
            return -2L;
        }

        if (detail.selectFirst("a.notify-request") != null) { // Available or not
            return -1L;
        }

        Element perc = detail.selectFirst("span.decrease_amount");
        return perc == null ? -1L : extractNumbers(perc.html());
    }

    @Override
    protected String getUrl() {
        return detail == null ? null : url;
    }

    @Override
    protected String getName() {
        return "maikonyv";
    }

}
