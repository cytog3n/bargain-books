package com.cyto.bargainbooks.request.handler;

public interface ListRequestHandler {

    /**
     * Callback with bookCount
     *
     * @param bookCount
     */
    void handleRequest(Integer bookCount);

}
