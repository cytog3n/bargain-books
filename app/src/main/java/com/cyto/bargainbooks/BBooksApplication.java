package com.cyto.bargainbooks;

import android.app.Application;

import com.cyto.bargainbooks.config.BookStoreList;
import com.cyto.bargainbooks.storage.BookSaleList;
import com.cyto.bargainbooks.storage.BookWishlist;
import com.cyto.bargainbooks.storage.Config;
import com.cyto.bargainbooks.store.BookStore;
import com.cyto.bargainbooks.store.impl.AlexandraBookStore;
import com.cyto.bargainbooks.store.impl.AlomgyarBookStore;
import com.cyto.bargainbooks.store.impl.Book24BookStore;
import com.cyto.bargainbooks.store.impl.BooklineBookStore;
import com.cyto.bargainbooks.store.impl.KonyvudvarBookStore;
import com.cyto.bargainbooks.store.impl.LibriBookStore;
import com.cyto.bargainbooks.store.impl.LiraBookStore;
import com.cyto.bargainbooks.store.impl.MaiKonyvBookStore;
import com.cyto.bargainbooks.store.impl.MolyBookStore;
import com.cyto.bargainbooks.store.impl.Numero7BookStore;
import com.cyto.bargainbooks.store.impl.ScolarBookStore;
import com.cyto.bargainbooks.store.impl.SzalayBookStore;
import com.cyto.bargainbooks.store.impl.Szazad21BookStore;
import com.cyto.bargainbooks.store.impl.TTKOnlineBookStore;

import java.util.Arrays;
import java.util.List;

public class BBooksApplication extends Application {

    private static BBooksApplication instance;

    public BBooksApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initStores();

        BookWishlist.initializeList(this);
        BookSaleList.initializeList(this);
        Config.getInstance(this);
    }

    private void initStores() {

        List<BookStore> stores = Arrays.asList(
                new AlexandraBookStore(),
                new AlomgyarBookStore(),
                new Book24BookStore(),
                new BooklineBookStore(),
                new KonyvudvarBookStore(),
                new LibriBookStore(),
                new LiraBookStore(),
                new MaiKonyvBookStore(),
                new MolyBookStore(),
                new Numero7BookStore(),
                new ScolarBookStore(),
                new SzalayBookStore(),
                new Szazad21BookStore(),
                new TTKOnlineBookStore()
        );

        BookStoreList.storeList.addAll(stores);
    }
}
