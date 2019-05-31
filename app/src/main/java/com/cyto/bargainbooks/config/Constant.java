package com.cyto.bargainbooks.config;

import java.util.LinkedHashMap;
import java.util.Map;

public class Constant {

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
}
