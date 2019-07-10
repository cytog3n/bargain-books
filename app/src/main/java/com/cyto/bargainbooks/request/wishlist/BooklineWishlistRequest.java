package com.cyto.bargainbooks.request.wishlist;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.cyto.bargainbooks.config.Constants;
import com.cyto.bargainbooks.factory.book.BookFactory;
import com.cyto.bargainbooks.factory.book.BooklineBookFactory;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class will handle the Bookline wishlists.
 * <p>
 * NOTE: This class will only query the first 50 page of the wishlist.
 */
public class BooklineWishlistRequest extends AbstractWishlistRequest {

    private final String baseUrl = "https://bookline.hu";

    private final List<String> pageUrls = new ArrayList<>();

    private final List<String> bookUrls = new ArrayList<>();

    private final BookFactory bookFactory = new BooklineBookFactory();

    private Long pageResponseCount;

    /**
     * Creates a new instance of BooklineWishlistRequest
     *
     * @param context     Will be used for the {@link VolleyService}
     * @param listHandler Will be used for informing the users about the size of the list
     * @param bh          Will handle the {@link com.cyto.bargainbooks.model.Book} entities
     * @param eh          Will handle the exceptions. Optional
     */
    public BooklineWishlistRequest(@NotNull Context context, ListRequestHandler listHandler, @NotNull BookHandler bh, ErrorHandler eh) {
        this.volleyService = VolleyService.getInstance(context);
        this.bookHandler = bh;
        this.errorHandler = eh;
        this.listHandler = listHandler;
    }

    /**
     * @see AbstractWishlistRequest#start(String)
     */
    @Override
    public void start(String url) {
        pageResponseCount = 1L; // Since the first query pulls the first 20 books, it's technicallly the first page
        pageUrls.clear();
        bookUrls.clear();
        volleyService.addToRequestQueue(new StringRequest(url, onResponse, error -> {
            Constants.errorListener.onErrorResponse(error);
            if (errorHandler != null) {
                errorHandler.handleError(error);
            }
        }).setRetryPolicy(Constants.requestPolicy));
    }

    /**
     * The {@link com.android.volley.Response.Listener} for the first call. The response will handle the pagination as well (if needed)
     */
    private Response.Listener<String> onResponse = response -> {

        Document doc = Jsoup.parse(response);
        addBooks(doc);

        Element element = doc.selectFirst("nav.o-pagination.l-align-right");
        if (element != null) {

            Element elem = element.selectFirst("a.o-pagination__btn");
            String url = baseUrl + elem.attr("href");

            for (int i = 2; i < 50; i++) { // Since there is no sing of pageCount, I use 50 (I could search the last one, but it would not fit here )
                pageUrls.add(url.replaceFirst("page=(\\d+)", "page=" + i));
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
                if (pageResponseCount == pageUrls.size()) {
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

    /**
     * Adding the bookUrls from the given page.
     *
     * @param doc Given page
     */
    private void addBooks(Document doc) {
        Elements list = doc.select("a.o-product__title");
        for (Element elem : list) {
            bookUrls.add(baseUrl + elem.attr("href"));
        }
    }

    /**
     * Start the query with the bookUrls (and will handle the {@link com.cyto.bargainbooks.model.Book} entities in the response )
     */
    private void queryBooks() {

        filterBookUrls();

        if (listHandler != null) {
            listHandler.handleRequest(bookUrls.size());
        }

        for (String s : bookUrls) {
            volleyService.addToRequestQueue(new StringRequest(s, response -> {
                bookHandler.handleBook(bookFactory.createBook(response));
            }, error -> {
                Constants.errorListener.onErrorResponse(error);
                if (errorHandler != null) {
                    errorHandler.handleError(error);
                }
            }).setRetryPolicy(Constants.requestPolicy));
        }
    }

    /**
     * Removes every duplication from the urls
     */
    private void filterBookUrls() {

        Set<String> books = new HashSet<>();
        // https://bookline.hu/product/home.action?_v=Aaron_Rosenberg_A_pengek_kiralynoje&type=10&id=2105787927&ca=PRODUCTSHELF
        String regex = "_v=(\\w+)&";
        Pattern p = Pattern.compile(regex);

        for (int i = 0; i < bookUrls.size(); i++) {
            Matcher m = p.matcher(bookUrls.get(i));
            if (m.find()) {
                if (!books.contains(m.group(1))) {
                    books.add(m.group(1));
                } else {
                    bookUrls.remove(i);
                    i--;
                }
            }
        }
    }
}
