package com.cyto.bargainbooks.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cyto.bargainbooks.R;
import com.cyto.bargainbooks.config.Constants;
import com.cyto.bargainbooks.factory.RequestFactory;
import com.cyto.bargainbooks.model.Book;
import com.cyto.bargainbooks.request.handler.BookHandler;
import com.cyto.bargainbooks.request.handler.ErrorHandler;
import com.cyto.bargainbooks.service.VolleyService;
import com.cyto.bargainbooks.storage.BookSaleList;
import com.cyto.bargainbooks.storage.BookWishlist;
import com.cyto.bargainbooks.util.BookFilter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BookDetailFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private View view;

    private ProgressBar progressBar;

    private Integer resCount = 0;

    private Integer reqCount = 0;

    private Date startDate;

    private Book book;

    private List<Book> bookList = new ArrayList<>();

    private final SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");

    public BookDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BookDetailFragment.
     */
    public static BookDetailFragment newInstance() {
        BookDetailFragment fragment = new BookDetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.i("BookDetailFragment", "Created");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.book_detail_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        bookList.clear();

        if (id == R.id.action_refresh) {

            VolleyService vs = VolleyService.getInstance(getContext());
            startDate = new Date();
            RequestFactory rf = new RequestFactory(getContext());
            progressBar.setVisibility(View.VISIBLE);
            List<StringRequest> srs = rf.getRequests(book, bh, eh);
            for (StringRequest sr : srs) {
                vs.addToRequestQueue(sr.setRetryPolicy(Constants.requestPolicy));
            }
            reqCount += srs.size();
            progressBar.setMax(reqCount);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_book_detail, container, false);
        ((NavigationView) getActivity().findViewById(R.id.nav_view)).setCheckedItem(R.id.nav_wishlist);
        TextView title = view.findViewById(R.id.title);
        TextView author = view.findViewById(R.id.author);
        TextView updateDate = view.findViewById(R.id.last_updated_value);
        TextView isbn = view.findViewById(R.id.isbn_value);
        LinearLayout stores = view.findViewById(R.id.stores);
        progressBar = view.findViewById(R.id.progressBar);
        String bisbn = this.getArguments().getString("isbn");
        Optional<Book> optBook = BookWishlist.getBooks().stream().filter(book1 -> book1.getISBN().equals(bisbn)).findFirst();

        if (optBook.isPresent()) {
            book = optBook.get();
            title.setText(book.getTitle());
            author.setText(book.getAuthor());
            updateDate.setText(book.getLastUpdateDate() == null ? "" : format.format(book.getLastUpdateDate()));
            isbn.setText(book.getISBN());
        } else {
            Toast.makeText(getContext(), "No ISBN? ", Toast.LENGTH_LONG).show();
        }

        populateStores(inflater, stores);
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
        Log.i("BookDetailFragment", "Attached");
        if (context instanceof BookDetailFragment.OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i("BookDetailFragment", "Detached");
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
     * Populate the stores into the given LinearLayout
     */
    private void populateStores(LayoutInflater inflater, LinearLayout stores) {
        BookFilter bf = new BookFilter(getContext());
        bookList.clear();
        bookList = BookSaleList.getBooks().stream().filter(book1 -> book1.getISBN().equals(book.getISBN())).sorted(Comparator.comparing(Book::getSalePercent).reversed()).collect(Collectors.toCollection(ArrayList::new));
        bookList = bf.filterBooks(bookList);

        for (Book b : bookList) {
            View view = inflater.inflate(R.layout.book_detail_item, stores, false);

            view.setOnClickListener(v -> {
                if (b.getUrl() == null) {
                    Snackbar.make(view, getContext().getText(R.string.no_available_store), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(b.getUrl()));
                    startActivity(intent);
                }
            });

            ((TextView) view.findViewById(R.id.shopName)).setText(Constants.storeMap.get(b.getStore()));
            ((TextView) view.findViewById(R.id.price)).setText(String.valueOf(b.getNewPrice()));
            ((TextView) view.findViewById(R.id.off)).setText(String.format(getContext().getString(R.string.sale_percent), b.getSalePercent()));
            stores.addView(view);
        }
    }

    private void onAllRequestComplete() {

        Optional<Book> wlBook = BookWishlist.getBooks().stream().filter(book1 -> book1.getISBN().equals(book.getISBN())).findFirst();
        if (wlBook.isPresent()) {
            wlBook.get().setLastUpdateDate(startDate);
            BookWishlist.saveListToSharedPreferences(getContext());
        }

        BookSaleList.getBooks().removeIf(book1 -> book1.getISBN().equals(book.getISBN()));
        BookSaleList.getBooks().addAll(bookList);
        BookSaleList.saveListToSharedPreferences(getContext());

        progressBar.setVisibility(View.GONE);
        reqCount = 0;
        resCount = 0;
        Date endDate = new Date();
        long diff = (endDate.getTime() - startDate.getTime()) / 1000;
        Log.d("Request time", diff / 1000 + " seconds");
        Snackbar.make(view, String.format(getContext().getString(R.string.query_finished_sec), diff), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        refreshFragment();
    }

    private final BookHandler bh = new BookHandler() {
        @Override
        public void handleBook(Book b) {
            resCount++;
            if (b.getNewPrice() > 0 && b.getSalePercent() > 0) {
                bookList.add(b);
            }

            progressBar.setProgress(resCount);

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

    /**
     * Refreshes the fragment, so the onCreateView method will initialize the whole fragment.
     */
    private void refreshFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }
        ft.detach(this).attach(this).commit();
    }
}
