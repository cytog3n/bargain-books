package com.cyto.bargainbooks.factory;

import android.content.Context;

import com.android.volley.toolbox.StringRequest;
import com.cyto.bargainbooks.config.Constants;
import com.cyto.bargainbooks.model.Book;
import com.cyto.bargainbooks.request.AlexandraRequest;
import com.cyto.bargainbooks.request.AlomgyarRequest;
import com.cyto.bargainbooks.request.Book24Request;
import com.cyto.bargainbooks.request.BooklineRequest;
import com.cyto.bargainbooks.request.KonyvudvarRequest;
import com.cyto.bargainbooks.request.LibriRequest;
import com.cyto.bargainbooks.request.LiraRequest;
import com.cyto.bargainbooks.request.MaiKonyvRequest;
import com.cyto.bargainbooks.request.MolyBoltRequest;
import com.cyto.bargainbooks.request.Numero7Request;
import com.cyto.bargainbooks.request.ScolarRequest;
import com.cyto.bargainbooks.request.SzalayKonyvekRequest;
import com.cyto.bargainbooks.request.Szazad21Request;
import com.cyto.bargainbooks.request.TTKOnlineRequest;
import com.cyto.bargainbooks.request.handler.BookHandler;
import com.cyto.bargainbooks.request.handler.ErrorHandler;
import com.cyto.bargainbooks.storage.Config;

import java.util.ArrayList;
import java.util.List;

public class RequestFactory {

    private final Context context;

    public RequestFactory(Context context) {
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
                        requests.add(new AlexandraRequest(b, bh, eh).getStringRequest());
                        break;
                    case "alomgyar":
                        requests.add(new AlomgyarRequest(b, bh, eh).getStringRequest());
                        break;
                    case "book24":
                        requests.add(new Book24Request(b, bh, eh).getStringRequest());
                        break;
                    case "bookline":
                        requests.add(new BooklineRequest(b, bh, eh).getStringRequest());
                        break;
                    case "konyvudvar":
                        requests.add(new KonyvudvarRequest(b, bh, eh).getStringRequest());
                        break;
                    case "libri":
                        requests.add(new LibriRequest(b, bh, eh).getStringRequest());
                        break;
                    case "lira":
                        requests.add(new LiraRequest(b, bh, eh).getStringRequest());
                        break;
                    case "maikonyv":
                        requests.add(new MaiKonyvRequest(b, bh, eh).getStringRequest());
                        break;
                    case "molybolt":
                        requests.add(new MolyBoltRequest(b, bh, eh).getStringRequest());
                        break;
                    case "numero7":
                        requests.add(new Numero7Request(b, bh, eh).getStringRequest());
                        break;
                    case "scolar":
                        requests.add(new ScolarRequest(b, bh, eh).getStringRequest());
                        break;
                    case "szalay":
                        requests.add(new SzalayKonyvekRequest(b, bh, eh).getStringRequest());
                        break;
                    case "szazad21":
                        requests.add(new Szazad21Request(b, bh, eh).getStringRequest());
                        break;
                    case "ttkonline":
                        requests.add(new TTKOnlineRequest(b, bh, eh).getStringRequest());
                        break;
                    default: //nop
                        break;
                }
            }
        }

        return requests;
    }

}
