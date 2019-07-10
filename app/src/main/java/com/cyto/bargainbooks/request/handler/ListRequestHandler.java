package com.cyto.bargainbooks.request.handler;

/**
 * This interface is used as a callback in the {@link com.android.volley.Response.Listener} in case of Wishlists.
 * <br>
 * Can be used for provide information for the user. (ex. set the {@link android.widget.ProgressBar} max value)
 */
public interface ListRequestHandler {

    /**
     * Callback with bookCount
     *
     * @param bookCount
     */
    void handleRequest(Integer bookCount);

}
