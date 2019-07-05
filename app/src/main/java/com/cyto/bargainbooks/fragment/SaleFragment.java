package com.cyto.bargainbooks.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
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

import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cyto.bargainbooks.R;
import com.cyto.bargainbooks.adapter.ExpandableBookListAdapter;
import com.cyto.bargainbooks.adapter.ExpandableStoreListAdapter;
import com.cyto.bargainbooks.config.Constants;
import com.cyto.bargainbooks.factory.BookRequestFactory;
import com.cyto.bargainbooks.model.Book;
import com.cyto.bargainbooks.request.handler.BookHandler;
import com.cyto.bargainbooks.request.handler.ErrorHandler;
import com.cyto.bargainbooks.service.VolleyService;
import com.cyto.bargainbooks.storage.BookSaleList;
import com.cyto.bargainbooks.storage.BookWishlist;
import com.cyto.bargainbooks.storage.Config;
import com.cyto.bargainbooks.util.BookFilter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SaleFragment extends Fragment {

    private SaleFragment.OnFragmentInteractionListener mListener;

    private ExpandableListView expandableListView;

    private ProgressBar progressBar;

    private ExpandableBookListAdapter expandableBookListAdapter;

    private Integer reqCount = 0;

    private Integer resCount = 0;

    private Date startDate;

    private final List<Pair> expandableListTitle = new ArrayList<>();

    private final Map<String, List<Book>> expandableListDetail = new HashMap<>();

    private List<Book> responseBooks = new ArrayList<>();

    private BookRequestFactory bookRequestFactory;

    private Boolean sort = true; // true -> %; false -> alphabetically

    private String saleLevel;

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
        Config config = Config.getInstance(getContext());
        saleLevel = config.getSaleLevel();
        Log.i("SaleFragment", "Created");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.sale_menu, menu);
        if (saleLevel.equals(Constants.storeLevel)) {
            menu.removeItem(R.id.action_change_store_view);
            menu.removeItem(R.id.sort);
        } else if (saleLevel.equals(Constants.bookLevel)) {
            menu.removeItem(R.id.action_change_book_view);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_clear) {

            new AlertDialog.Builder(getContext()).setTitle(getString(R.string.clear_confirm_title))
                    .setMessage(getString(R.string.clear_confirm_text))
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            BookSaleList.clear();
                            BookSaleList.saveListToSharedPreferences(getContext());
                            refreshFragment();
                        }
                    })
                    .setNegativeButton(getString(R.string.no), null)
                    .show();
            return true;
        } else if (id == R.id.action_refresh) {
            startDate = new Date();
            responseBooks.clear();
            reqCount = 0;
            resCount = 0;
            VolleyService vs = VolleyService.getInstance(getContext());
            for (Book b : BookWishlist.getBooks()) {
                List<StringRequest> reqs = bookRequestFactory.getRequests(b, bh, eh);
                reqCount += reqs.size();
                for (StringRequest req : reqs) {
                    vs.addToRequestQueue(req.setRetryPolicy(Constants.requestPolicy).setTag(Constants.SALE_TAG));
                }
            }

            progressBar.setMax(reqCount);
            progressBar.setVisibility(View.VISIBLE);
        } else if (id == R.id.action_change_book_view) {
            saleLevel = Constants.bookLevel;
            refreshFragment();
        } else if (id == R.id.action_change_store_view) {
            saleLevel = Constants.storeLevel;
            refreshFragment();
        } else if (id == R.id.sort) {
            if (saleLevel.equals(Constants.bookLevel)) {
                if (!sort) {
                    expandableListTitle.sort(Comparator.comparing(pair -> 100 - ((Book) pair.second).getSalePercent()));
                    Snackbar.make(getActivity().getCurrentFocus(), R.string.sorted_discount, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    sort = !sort;
                } else {
                    expandableListTitle.sort(Comparator.comparing(pair -> ((Book) pair.second).getTitle()));
                    Snackbar.make(getActivity().getCurrentFocus(), R.string.sorted_alphabet, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    sort = !sort;
                }
                expandableBookListAdapter.notifyDataSetChanged();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sale, container, false);
        ((NavigationView) getActivity().findViewById(R.id.nav_view)).setCheckedItem(R.id.nav_sale);

        expandableListTitle.clear();
        expandableListDetail.clear();

        expandableListView = view.findViewById(R.id.expandableListView);
        progressBar = view.findViewById(R.id.progressBar);
        bookRequestFactory = new BookRequestFactory(getContext());

        if (saleLevel.equals("BOOK_LEVEL")) {
            bookLevelList(view);
        } else if (saleLevel.equals("STORE_LEVEL")) {
            storeLevelList(view);
        }
        return view;
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
        if (context instanceof OnFragmentInteractionListener) {
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

    /**
     * Refreshes the fragment, so the onCreateView method will initialize the whole fragment.
     */
    private void refreshFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }

        for (int i = 0; i < expandableListTitle.size(); i++) {
            if (expandableListView.isGroupExpanded(i)) {
                expandableListView.collapseGroup(i);
            }
        }

        expandableListTitle.clear();
        expandableListDetail.clear();
        ft.detach(this).attach(this).commit();
    }

    /**
     * This method handles the BookLists after all of the request are returned.
     */
    private void onAllRequestComplete() {
        Date endDate = new Date();
        long diff = (endDate.getTime() - startDate.getTime()) / 1000;
        Log.d("Request time", diff / 1000 + " seconds");
        Snackbar.make(getActivity().getCurrentFocus(), String.format(getContext().getString(R.string.query_finished_sec), diff), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        if (responseBooks.size() > 0) {
            BookSaleList.save(responseBooks);
            BookSaleList.saveListToSharedPreferences(getContext());
            BookWishlist.saveListToSharedPreferences(getContext()); //save the updateDate
        } else {
            Snackbar.make(getActivity().getCurrentFocus(), "Please check your network", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

        progressBar.setVisibility(View.GONE);
        refreshFragment();
    }

    private void bookLevelList(View view) {

        BookFilter bf = new BookFilter(getContext());
        List<Book> books = bf.filterBooks(BookSaleList.getBooks());
        for (Book book : books) {
            String isbn = book.getISBN();
            if (expandableListDetail.get(isbn) == null) {
                expandableListDetail.put(isbn, new ArrayList<>());
            }
            expandableListDetail.get(isbn).add(book);
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
        expandableBookListAdapter = new ExpandableBookListAdapter(getContext(), expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableBookListAdapter);
        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {

            Book b = expandableListDetail.get(expandableListTitle.get(groupPosition).first).get(childPosition);

            if (b.getUrl() == null) {
                Snackbar.make(getActivity().getCurrentFocus(), getContext().getText(R.string.no_available_store), Snackbar.LENGTH_LONG)
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

        BookFilter bf = new BookFilter(getContext());
        List<Book> books = bf.filterBooks(BookSaleList.getBooks());
        for (Book book : books) {
            String store = book.getStore();
            if (expandableListDetail.get(store) == null) {
                expandableListDetail.put(store, new ArrayList<>());
            }
            expandableListDetail.get(store).add(book);
        }

        for (String s : expandableListDetail.keySet()) {
            expandableListDetail.get(s).sort(Comparator.comparing(Book::getSalePercent).reversed());
            expandableListTitle.add(new Pair(s, expandableListDetail.get(s).size()));
        }

        // DESC
        expandableListTitle.sort((o1, o2) -> Integer.compare((int) o2.second, (int) o1.second));

        ExpandableStoreListAdapter expandableStoreListAdapter = new ExpandableStoreListAdapter(getContext(), getActivity(), expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableStoreListAdapter);
        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {

            Book b = expandableListDetail.get(expandableListTitle.get(groupPosition).first).get(childPosition);

            if (b.getUrl() == null) {
                Snackbar.make(getActivity().getCurrentFocus(), getContext().getText(R.string.no_available_store), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(b.getUrl()));
                startActivity(intent);
            }

            return false;
        });
    }

    private final BookHandler bh = new BookHandler() {
        @Override
        public void handleBook(Book b) {
            resCount++;
            if (b.getNewPrice() > 0 && b.getSalePercent() > 0) {
                responseBooks.add(b);
            }

            progressBar.setProgress(resCount);
            Optional<Book> wlBook = BookWishlist.getBooks().stream().filter(book -> b.getISBN().equals(book.getISBN())).findFirst();
            wlBook.ifPresent(book -> book.setLastUpdateDate(startDate));
            if (resCount.equals(reqCount)) {
                onAllRequestComplete();
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
                onAllRequestComplete();
            }

            Log.d("Request list", reqCount + "/" + resCount);
        }
    };
}
