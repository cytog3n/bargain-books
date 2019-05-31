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
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Szazad21Request extends AbstractRequest {

    private Element detail;

    private final BookHandler bookHandler;

    private final ErrorHandler errorHandler;

    private Book book;

    private String url = null;

    public Szazad21Request(@NotNull Book book, @NotNull BookHandler bh, ErrorHandler eh) {
        this.book = book;
        this.bookHandler = bh;
        this.errorHandler = eh;
    }

    @Override
    public StringRequest getStringRequest() {

        Map<String, String> params = new HashMap<>();
        params.put("isbn", book.getISBN());
        String search = "https://21.szazadkiado.hu/index.php?route=product/list&keyword=${isbn}";
        String url = UrlUtil.ApplyParameters(search, params);

        return new StringRequest(url, listener, failedRequestListener);
    }

    private final Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Document doc = Jsoup.parse(response);
            Elements list = doc.select("table.product_table");

            if (list.size() == 0) {
                detail = null;
            } else {
                detail = list.first();
            }

            if (detail != null) {
                String urlRegex = "<link href=\"(.+)\" rel=\"canonical\">";
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

        Element price = detail.selectFirst("span.price.price_original_color.product_table_original");
        return price == null ? -1L : extractNumbers(price.html());
    }

    @Override
    protected Long getBookNewPrice() {

        if (detail == null) {
            return -2L;
        }

        Element price = detail.selectFirst("span.price.price_special_color.product_table_special");
        return price == null ? -1L : extractNumbers(price.html());
    }

    @Override
    protected Long getSalePercent() {

        if (detail == null) {
            return -2L;
        }

        Elements list = detail.select("span.decrease_amount");
        Element perc = list.get(1);
        return perc == null ? -1L : extractNumbers(perc.html());
    }

    @Override
    protected String getUrl() {
        return detail == null ? null : url;
    }

    @Override
    protected String getName() {
        return "szazad21";
    }

}
