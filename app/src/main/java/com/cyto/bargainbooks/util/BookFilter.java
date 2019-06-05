package com.cyto.bargainbooks.util;

import android.content.Context;

import com.cyto.bargainbooks.model.Book;
import com.cyto.bargainbooks.storage.Config;

import java.util.ArrayList;
import java.util.List;

public class BookFilter {

    private final Context context;

    public BookFilter(Context context) {
        this.context = context;
    }

    public List<Book> filterBooks(List<Book> input) {
        Config config = Config.getInstance(context);
        List<Book> output = new ArrayList<>();

        input.forEach(book -> {
            Boolean b = config.getStoreFilter().get(book.getStore());
            if (b != null && b) {
                if (!book.getStore().equals("libri")) {
                    output.add(book);
                } else if (config.getShowLibri5PercentDeals()) {
                    output.add(book);
                } else if (book.getSalePercent() > 5L) {
                    output.add(book);
                }
            }
        });

        return output;
    }

}
