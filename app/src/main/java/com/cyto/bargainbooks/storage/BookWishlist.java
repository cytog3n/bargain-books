package com.cyto.bargainbooks.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.cyto.bargainbooks.model.Book;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public final class BookWishlist {

    private static final Type listType = new TypeToken<ArrayList<Book>>() {
    }.getType();

    private static List<Book> bookList;

    public static void initializeList(Context context) {
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = context.getSharedPreferences("BargainBooks", Context.MODE_PRIVATE);
        String wishListJson = sharedPreferences.getString("WishList", null);

        if (wishListJson == null) {
            bookList = new ArrayList<>();
        } else {
            bookList = gson.fromJson(wishListJson, listType);
        }

    }

    public static List<Book> getBooks() {
        return bookList;
    }

    public static void saveListToSharedPreferences(Context context) {
        bookList.sort(Comparator.comparing(Book::getTitle));
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = context.getSharedPreferences("BargainBooks", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("WishList", gson.toJson(bookList));
        editor.apply();
    }

    public static void saveBooks(List<Book> b) {
        bookList.clear();
        bookList.addAll(b);
    }

    public static void mergeBooks(List<Book> b) {
        b.forEach(obook -> {
            Optional<Book> ob = bookList.stream().filter(book -> book.getISBN().equals(obook.getISBN())).findFirst();
            if (!ob.isPresent()) {
                bookList.add(obook);
            }
        });
    }

    public static void clear() {
        bookList.clear();
    }

}
