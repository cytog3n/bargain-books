package com.cyto.bargainbooks.util;

import android.content.Context;

import com.cyto.bargainbooks.storage.Config;
import com.cyto.bargainbooks.model.Book;

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
                output.add(book);
            }
        });

        return output;
    }

}
