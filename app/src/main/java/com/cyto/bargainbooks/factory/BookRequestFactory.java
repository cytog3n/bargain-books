package com.cyto.bargainbooks.factory;

import android.content.Context;

import com.android.volley.toolbox.StringRequest;
import com.cyto.bargainbooks.config.Constants;
import com.cyto.bargainbooks.model.Book;
import com.cyto.bargainbooks.request.AlexandraBookRequest;
import com.cyto.bargainbooks.request.AlomgyarBookRequest;
import com.cyto.bargainbooks.request.Book24BookRequest;
import com.cyto.bargainbooks.request.BooklineBookRequest;
import com.cyto.bargainbooks.request.KonyvudvarBookRequest;
import com.cyto.bargainbooks.request.LibriBookRequest;
import com.cyto.bargainbooks.request.LiraBookRequest;
import com.cyto.bargainbooks.request.MaiKonyvBookRequest;
import com.cyto.bargainbooks.request.MolyBoltBookRequest;
import com.cyto.bargainbooks.request.Numero7BookRequest;
import com.cyto.bargainbooks.request.ScolarBookRequest;
import com.cyto.bargainbooks.request.SzalayKonyvekBookRequest;
import com.cyto.bargainbooks.request.Szazad21BookRequest;
import com.cyto.bargainbooks.request.TTKOnlineBookRequest;
import com.cyto.bargainbooks.request.handler.BookHandler;
import com.cyto.bargainbooks.request.handler.ErrorHandler;
import com.cyto.bargainbooks.storage.Config;

import java.util.ArrayList;
import java.util.List;

public class BookRequestFactory {

    private final Context context;

    public BookRequestFactory(Context context) {
        this.context = context;
    }

    public List<StringRequest> getRequests(Book b, BookHandler bh, ErrorHandler eh) {
        List<StringRequest> requests = new ArrayList<>();
        Config c = Config.getInstance(context);

        for (String storeName : Constants.storeMap.keySet()) {
            Boolean filterOn = c.getStoreFilter().get(storeName);
            if (filterOn) {
                switch (storeName) {
                    case "alexandra":
                        requests.add(new AlexandraBookRequest(b, bh, eh).getStringRequest());
                        break;
                    case "alomgyar":
                        requests.add(new AlomgyarBookRequest(b, bh, eh).getStringRequest());
                        break;
                    case "book24":
                        requests.add(new Book24BookRequest(b, bh, eh).getStringRequest());
                        break;
                    case "bookline":
                        requests.add(new BooklineBookRequest(b, bh, eh).getStringRequest());
                        break;
                    case "konyvudvar":
                        requests.add(new KonyvudvarBookRequest(b, bh, eh).getStringRequest());
                        break;
                    case "libri":
                        requests.add(new LibriBookRequest(b, bh, eh).getStringRequest());
                        break;
                    case "lira":
                        requests.add(new LiraBookRequest(b, bh, eh).getStringRequest());
                        break;
                    case "maikonyv":
                        requests.add(new MaiKonyvBookRequest(b, bh, eh).getStringRequest());
                        break;
                    case "molybolt":
                        requests.add(new MolyBoltBookRequest(b, bh, eh).getStringRequest());
                        break;
                    case "numero7":
                        requests.add(new Numero7BookRequest(b, bh, eh).getStringRequest());
                        break;
                    case "scolar":
                        requests.add(new ScolarBookRequest(b, bh, eh).getStringRequest());
                        break;
                    case "szalay":
                        requests.add(new SzalayKonyvekBookRequest(b, bh, eh).getStringRequest());
                        break;
                    case "szazad21":
                        requests.add(new Szazad21BookRequest(b, bh, eh).getStringRequest());
                        break;
                    case "ttkonline":
                        requests.add(new TTKOnlineBookRequest(b, bh, eh).getStringRequest());
                        break;
                    default: //nop
                        break;
                }
            }
        }

        return requests;
    }

}
