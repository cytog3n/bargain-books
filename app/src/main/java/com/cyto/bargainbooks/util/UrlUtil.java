package com.cyto.bargainbooks.util;

import java.util.Map;

public class UrlUtil {

    private final static String PATTERN = ".*\\$\\{.+?\\}.*";

    public static String ApplyParameters(String URL, Map<String, String> pairs) {

        if (URL != null && pairs != null) {
            if (URL.matches(PATTERN)) {

                for (String key : pairs.keySet()) {
                    URL = URL.replaceAll("\\$\\{" + key + "\\}", pairs.get(key));
                }

                if (URL.matches(PATTERN)) {
                    throw new IllegalArgumentException("There is at least one unmatched parameter in the URL: " + URL);
                }
            }
        }
        return URL;
    }
}
