package com.cyto.bargainbooks.util;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

public class RequestCountByTagFilter implements RequestQueue.RequestFilter {

    private Integer count = 0;

    private String tag;

    public RequestCountByTagFilter(String tag) {
        this.tag = tag;
    }

    @Override
    public boolean apply(Request<?> request) {
        if (request.getTag().toString().equals(tag)) {
            count++;
        }
        return false; // aways return false, we don't want to cancel any of the requests
    }

    public int getCount() {
        return count;
    }

}