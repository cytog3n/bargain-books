package com.cyto.bargainbooks.util;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

public class RequestCountFilter implements RequestQueue.RequestFilter {

    private Integer count = 0;

    @Override
    public boolean apply(Request<?> request) {
        count++;
        return false; // aways return false, we don't want to cancel any of the requests
    }

    public int getCount() {
        return count;
    }

}
