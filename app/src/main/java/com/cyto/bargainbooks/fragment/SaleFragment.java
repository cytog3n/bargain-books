package com.cyto.bargainbooks.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cyto.bargainbooks.R;
import com.cyto.bargainbooks.adapter.ExpandableBookListAdapter;
import com.cyto.bargainbooks.adapter.ExpandableStoreListAdapter;
import com.cyto.bargainbooks.storage.Config;
import com.cyto.bargainbooks.config.Constants;
import com.cyto.bargainbooks.factory.RequestFactory;
import com.cyto.bargainbooks.model.Book;
import com.cyto.bargainbooks.request.handler.BookHandler;
import com.cyto.bargainbooks.request.handler.ErrorHandler;
import com.cyto.bargainbooks.service.VolleyService;
import com.cyto.bargainbooks.storage.BookSaleList;
import com.cyto.bargainbooks.storage.BookWishlist;
import com.cyto.bargainbooks.util.BookFilter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaleFragment extends Fragment {

    private SaleFragment.OnFragmentInteractionListener mListener;

    private final List<Pair> expandableListTitle = new ArrayList<>();

    private final Map<String, List<Book>> expandableListDetail = new HashMap<>();

    private ExpandableListView expandableListView;

    private ProgressBar progressBar;

    private Integer reqCount = 0;

    private Integer resCount = 0;

    private Date startDate;

    private Date endDate;

    private List<Book> responseBooks;

    private final RetryPolicy policy = new DefaultRetryPolicy(5000, 3, 2.0f);

    private RequestFactory requestFactory;

    private Config config;

    private Context context;

    private View view;

    private ExpandableBookListAdapter expandableBookListAdapter;

    private Boolean sort = true; // true -> %; false -> alphabetically

    public SaleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SaleFragment.
     */
    public static SaleFragment newInstance() {
        SaleFragment fragment = new SaleFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.i("SaleFragment", "Created");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.sale_menu, menu);
        if (config.getSaleLevel().equals(Constants.storeLevel)) {
            menu.removeItem(R.id.action_change_store_view);
            menu.removeItem(R.id.sort);
        } else if (config.getSaleLevel().equals(Constants.bookLevel)) {
            menu.removeItem(R.id.action_change_book_view);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_clear) {
            BookSaleList.clear();
            BookSaleList.saveListToSharedPreferences(getContext());
            refreshFragment();
            return true;
        } else if (id == R.id.action_refresh) {
            startDate = new Date();
            responseBooks = new ArrayList<>();
            VolleyService vs = VolleyService.getInstance(getContext());
            for (Book b : BookWishlist.getBooks()) {
                b.setLastUpdateDate(startDate);
                List<StringRequest> reqs = requestFactory.getRequests(b, bh, eh);
                reqCount += reqs.size();
                for (StringRequest req : reqs) {
                    vs.addToRequestQueue(req.setRetryPolicy(policy));
                }
            }

            progressBar.setMax(reqCount);
            progressBar.setVisibility(View.VISIBLE);
        } else if (id == R.id.action_change_book_view) {
            if (config.getSaleLevel().equals(Constants.storeLevel)) {
                config.setSaleLevel(Constants.bookLevel);
                refreshFragment();
            }
        } else if (id == R.id.action_change_store_view) {
            if (config.getSaleLevel().equals(Constants.bookLevel)) {
                config.setSaleLevel(Constants.storeLevel);
                refreshFragment();
            }
        } else if (id == R.id.sort) {
            if (config.getSaleLevel().equals(Constants.bookLevel)) {
                if (!sort) {
                    expandableListTitle.sort(Comparator.comparing(pair -> 100 - ((Book) pair.second).getSalePercent()));
                    sort = !sort;
                } else {
                    expandableListTitle.sort(Comparator.comparing(pair -> ((Book) pair.second).getTitle()));
                    sort = !sort;
                }
                expandableBookListAdapter.notifyDataSetChanged();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private final BookHandler bh = new BookHandler() {
        @Override
        public void handleBook(Book b) {
            resCount++;
            if (b.getNewPrice() > 0 && b.getSalePercent() > 0) {
                responseBooks.add(b);
            }

            progressBar.setProgress(resCount);

            if (resCount.equals(reqCount)) {
                endDate = new Date();
                Long diff = endDate.getTime() - startDate.getTime();
                Log.d("Request time", diff + " ms");
                Snackbar.make(view, "Finished in  " + diff + "  ms", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                BookSaleList.save(responseBooks);
                BookSaleList.saveListToSharedPreferences(getContext());
                BookWishlist.saveListToSharedPreferences(getContext()); //save the updateDate
                progressBar.setVisibility(View.GONE);
                refreshFragment();
            }

            Log.d("Request list", reqCount + "/" + resCount);
        }
    };

    private final ErrorHandler eh = new ErrorHandler() {
        @Override
        public void handleError(VolleyError error) {
            resCount++;

            progressBar.setProgress(resCount);

            if (resCount.equals(reqCount)) {
                endDate = new Date();
                Long diff = endDate.getTime() - startDate.getTime();
                Log.d("Request time", diff + " ms");
                Snackbar.make(view, "Finished in  " + diff + "  ms", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                BookSaleList.save(responseBooks);
                BookSaleList.saveListToSharedPreferences(getContext());
                BookWishlist.saveListToSharedPreferences(getContext()); //save the updateDate
                progressBar.setVisibility(View.GONE);
                refreshFragment();
            }

            Log.d("Request list", reqCount + "/" + resCount);
        }
    };

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_sale, container, false);
        expandableListView = view.findViewById(R.id.expandableListView);
        progressBar = view.findViewById(R.id.progressBar);
        context = getActivity().getApplicationContext();
        requestFactory = new RequestFactory(context);
        config = Config.getInstance(context);

        if (config.getSaleLevel().equals("BOOK_LEVEL")) {
            bookLevelList(view);
        } else if (config.getSaleLevel().equals("STORE_LEVEL")) {
            storeLevelList(view);
        }

        return view;
    }

    private void bookLevelList(View view) {

        BookFilter bf = new BookFilter(context);
        List<Book> books = bf.filterBooks(BookSaleList.getBooks());
        for (Book book : books) {
            String isbn = book.getISBN();
            if (expandableListDetail.get(isbn) == null) {
                expandableListDetail.put(isbn, new ArrayList<>());
            }

            if (config.getShowLibri5PercentDeals()) {
                expandableListDetail.get(isbn).add(book);
            } else {
                if (book.getStore().equals("libri") && book.getSalePercent() == 5L) {
                    // skip in that case
                } else {
                    expandableListDetail.get(isbn).add(book);
                }
            }
        }

        for (String s : expandableListDetail.keySet()) {

            List<Book> storeBooks = expandableListDetail.get(s);
            storeBooks.sort(Comparator.comparing(Book::getSalePercent).reversed());

            if (storeBooks.size() > 0) {
                Book b = expandableListDetail.get(s).get(0);
                if (b.getSalePercent() != null) {
                    for (Book book : expandableListDetail.get(s)) {
                        if (book.getSalePercent() > b.getSalePercent()) {
                            b = book;
                        }
                    }
                }

                /* Numero7 is kinda suck with the % calculation and don't have the OG price on the list view
                 * Workaround START */
                if (b.getStore() != null && b.getStore().equals("Numero7")) {
                    for (Book book : expandableListDetail.get(s)) {
                        if (book.getStore() != null && !book.getStore().equals("Numero7")) {
                            b.setOriginalPrice(book.getOriginalPrice());
                            break;
                        }
                    }
                }

                // If it's still -1, calculate a ~value
                if (b.getOriginalPrice() != null && b.getOriginalPrice() == -1) {
                    if (b.getSalePercent() != null && b.getNewPrice() != null && b.getSalePercent() > 0) {
                        Float f = (float) b.getNewPrice() / ((100 - b.getSalePercent()) / 100f);
                        b.setOriginalPrice(f.longValue());
                    } else {
                        b.setOriginalPrice(null);
                    }
                }
                /* Numero7 workaround END. */

                expandableListTitle.add(new Pair(s, b));
            }
        }
        expandableListTitle.sort(Comparator.comparing(pair -> 100 - ((Book) pair.second).getSalePercent()));
        expandableBookListAdapter = new ExpandableBookListAdapter(context, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableBookListAdapter);
        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {

            Book b = expandableListDetail.get(expandableListTitle.get(groupPosition).first).get(childPosition);

            if (b.getUrl() == null) {
                Snackbar.make(view, context.getText(R.string.no_available_store), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(b.getUrl()));
                startActivity(intent);
            }

            return false;
        });
    }

    private void storeLevelList(View view) {

        BookFilter bf = new BookFilter(context);
        List<Book> books = bf.filterBooks(BookSaleList.getBooks());
        for (Book book : books) {
            String store = book.getStore();
            if (expandableListDetail.get(store) == null) {
                expandableListDetail.put(store, new ArrayList<>());
            }

            if (config.getShowLibri5PercentDeals()) {
                expandableListDetail.get(store).add(book);
            } else {
                if (book.getStore().equals("libri") && book.getSalePercent() == 5L) {
                    // skip in that case
                } else {
                    expandableListDetail.get(store).add(book);
                }
            }

        }

        for (String s : expandableListDetail.keySet()) {
            //DESC
            expandableListDetail.get(s).sort((o1, o2) -> {
                if (o1.getSalePercent() > o2.getSalePercent()) {
                    return -1;
                } else if (o1.getSalePercent() < o2.getSalePercent()) {
                    return 1;
                } else {
                    return 0;
                }
            });
            expandableListTitle.add(new Pair(s, expandableListDetail.get(s).size()));
        }

        // DESC
        expandableListTitle.sort((o1, o2) -> {
            if ((int) o1.second > (int) o2.second) {
                return -1;
            } else if ((int) o1.second < (int) o2.second) {
                return 1;
            } else return 0;
        });
        ExpandableStoreListAdapter expandableStoreListAdapter = new ExpandableStoreListAdapter(context, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableStoreListAdapter);
        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {

            Book b = expandableListDetail.get(expandableListTitle.get(groupPosition).first).get(childPosition);

            if (b.getUrl() == null) {
                Snackbar.make(view, context.getText(R.string.no_available_store), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(b.getUrl()));
                startActivity(intent);
            }

            return false;
        });
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i("SaleFragment", "Attached");
        if (context instanceof SearchFragment.OnFragmentInteractionListener) {
            mListener = (SaleFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i("SaleFragment", "Detached");
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private void refreshFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }
        expandableListTitle.clear();
        expandableListDetail.clear();
        ft.detach(this).attach(this).commit();
    }
}
