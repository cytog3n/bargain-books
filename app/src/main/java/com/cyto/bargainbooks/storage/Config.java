package com.cyto.bargainbooks.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.cyto.bargainbooks.config.BookStoreList;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public final class Config {

    private static final Gson gson = new Gson();

    private static Config config = null;

    private String saleLevel = "BOOK_LEVEL"; // init

    private Boolean showLibri5PercentDeals = true; // init

    private Map<String, Boolean> storeFilter;

    private Config() {
    }

    public static Config getInstance(Context context) {
        if (config == null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("BargainBooks", Context.MODE_PRIVATE);
            String configJson = sharedPreferences.getString("Config", null);
            if (configJson != null) {
                config = gson.fromJson(configJson, Config.class);
            } else {
                config = new Config();
                HashMap<String, Boolean> map = new HashMap<>();
                for (String s : BookStoreList.getStoreKeys()) {
                    map.put(s, true);
                }
                config.storeFilter = map;
            }

            // Add the new ones
            if (config.getStoreFilter().size() != BookStoreList.getStoreKeys().size()) {
                for (String s : BookStoreList.getStoreKeys()) {
                    if (config.getStoreFilter().get(s) == null) {
                        config.getStoreFilter().put(s, true);
                    }
                }
            }

        }
        return config;
    }

    public static void saveConfigToSharedPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("BargainBooks", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Config", gson.toJson(config));
        editor.apply();
    }

    public String getSaleLevel() {
        return saleLevel;
    }

    public void setSaleLevel(String saleLevel) {
        this.saleLevel = saleLevel;
    }

    public Map<String, Boolean> getStoreFilter() {
        return storeFilter;
    }

    public Boolean getShowLibri5PercentDeals() {
        return showLibri5PercentDeals;
    }

    public void setShowLibri5PercentDeals(Boolean showLibri5PercentDeals) {
        this.showLibri5PercentDeals = showLibri5PercentDeals;
    }
}
