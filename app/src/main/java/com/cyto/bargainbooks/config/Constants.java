package com.cyto.bargainbooks.config;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RetryPolicy;

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

    /**
     * The default requestPolicy for the apllication. It has expanded timeout and retry.
     */
    public static final RetryPolicy requestPolicy = new DefaultRetryPolicy(5000, 3, 2.0f);

}
