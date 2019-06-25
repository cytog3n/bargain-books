package com.cyto.bargainbooks.config;

import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;

import java.util.LinkedHashMap;
import java.util.Map;

public class Constants {

    public static final String storeLevel = "STORE_LEVEL";

    public static final String bookLevel = "BOOK_LEVEL";

    public static final Map<String, String> storeMap = new LinkedHashMap<String, String>() {{
        put("alexandra", "Alexandra");
        put("alomgyar", "Álomgyár");
        put("book24", "Book24");
        put("bookline", "Bookline");
        put("konyvudvar", "Könyvudvar");
        put("libri", "Libri");
        put("lira", "Líra");
        put("maikonyv", "Mai könyv");
        put("molybolt", "Moly bolt");
        put("numero7", "Numero7");
        put("scolar", "Scolar kiadó");
        put("szalay", "Szalay Könyvkiadó");
        put("szazad21", "21. század");
        put("ttkonline", "TTK Online");
    }};

    public static final String SALE_TAG = "SALE";

    public static final String DETAIL_TAG = "DETAIL_%s";

    /**
     * The default requestPolicy for the apllication. It has expanded timeout and retry.
     */
    public static final RetryPolicy requestPolicy = new DefaultRetryPolicy(5000, 3, 2.0f);

    public static final Response.ErrorListener errorListener = error -> {
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            Log.e("TimeoutError | NoConnectionError", "Check your connection");
        } else if (error instanceof ServerError) {
            Log.e("NetworkError", "Server responded with an error response");
        } else if (error instanceof NetworkError) {
            Log.e("NetworkError", "There was a network error");
        } else if (error instanceof ParseError) {
            Log.e("ParseError", "Unable to parse the response");
        }
    };

}
