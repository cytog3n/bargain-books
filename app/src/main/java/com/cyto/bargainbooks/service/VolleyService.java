package com.cyto.bargainbooks.service;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleyService {

    private static VolleyService volleyService;

    private RequestQueue requestQueue;

    private static Context context;

    private VolleyService(Context context) {
        VolleyService.context = context;
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


}
