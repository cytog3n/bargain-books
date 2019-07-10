package com.cyto.bargainbooks.request.handler;

/**
 * This interface is used as a callback in the {@link com.android.volley.Response.ErrorListener}
 */
public interface ErrorHandler {

    /**
     * Callback with an {@link Exception}
     * @param error
     */
    void handleError(Exception error);

}
