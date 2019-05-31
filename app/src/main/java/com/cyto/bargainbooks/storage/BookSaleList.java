package com.cyto.bargainbooks.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.cyto.bargainbooks.model.Book;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public final class BookSaleList {

    private static final Type listType = new TypeToken<ArrayList<Book>>() {
    }.getType();

    private static List<Book> sales;

    public static void initializeList(@NotNull Context context) {
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = context.getSharedPreferences("BargainBooks", Context.MODE_PRIVATE);
        String salesJson = sharedPreferences.getString("Sales", null);

        if (salesJson == null) {
            sales = new ArrayList<>();
        } else {
            sales = gson.fromJson(salesJson, listType);
        }
    }

    public static List<Book> getBooks() {
        return sales;
    }

    public static void saveListToSharedPreferences(Context context) {
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = context.getSharedPreferences("BargainBooks", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Sales", gson.toJson(sales));
        editor.apply();
    }

    public static void save(List<Book> b) {
        sales.clear();
        sales.addAll(b);
    }

    public static void clear() {
        sales.clear();
    }

}
