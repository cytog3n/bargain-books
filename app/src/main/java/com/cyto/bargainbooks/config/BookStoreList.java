package com.cyto.bargainbooks.config;

import com.cyto.bargainbooks.store.BookStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BookStoreList {

    public static final List<BookStore> storeList = new ArrayList<>();

    public static BookStore getBookStoreByKey(String key) {

        Optional<BookStore> bookStore = Optional.empty();
        for (BookStore bookStore1 : storeList) {
            if (bookStore1.getStoreKey().equals(key)) {
                bookStore = Optional.of(bookStore1);
                break;
            }
        }
        return bookStore.isPresent() ? bookStore.get() : null;
    }

    public static List<String> getStoreKeys() {
        return storeList.stream().map(bookStore -> bookStore.getStoreKey()).collect(Collectors.toList());
    }

}
