package com.cyto.bargainbooks.service;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.cyto.bargainbooks.util.RequestCountByTagFilter;
import com.cyto.bargainbooks.util.RequestCountFilter;

public class VolleyService {

    private static VolleyService volleyService;

    private RequestQueue requestQueue;

    private final Context context;

    private final Integer c = 0;

    private VolleyService(Context context) {
        this.context = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized VolleyService getInstance(Context context) {
        if (volleyService == null) {
            volleyService = new VolleyService(context);
        }
        return volleyService;
    }

    private RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public Integer getActiveRequestCount() {
        RequestCountFilter rcf = new RequestCountFilter();
        requestQueue.cancelAll(rcf);
        return rcf.getCount();
    }

    public Integer getActiveRequestCountByTag(String tag) {
        RequestCountByTagFilter rcf = new RequestCountByTagFilter(tag);
        requestQueue.cancelAll(rcf);
        return rcf.getCount();
    }

    public Boolean isThereAnyActiveRequests() {
        return  getActiveRequestCount() != 0;
    }
}
