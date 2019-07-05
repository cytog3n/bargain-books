package com.cyto.bargainbooks.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.navigation.Navigation;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cyto.bargainbooks.R;
import com.cyto.bargainbooks.config.Constants;
import com.cyto.bargainbooks.model.Book;
import com.cyto.bargainbooks.parser.BooklineWishlistParser;
import com.cyto.bargainbooks.parser.LibriWishlistParser;
import com.cyto.bargainbooks.parser.MolyWishlistParser;
import com.cyto.bargainbooks.parser.Szazad21WishlistParser;
import com.cyto.bargainbooks.request.handler.BookHandler;
import com.cyto.bargainbooks.request.handler.ErrorHandler;
import com.cyto.bargainbooks.request.handler.ListRequestHandler;
import com.cyto.bargainbooks.service.VolleyService;
import com.cyto.bargainbooks.storage.BookSaleList;
import com.cyto.bargainbooks.storage.BookWishlist;

import org.apache.commons.collections4.map.HashedMap;

import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ImportOnlineWishlistFragment extends Fragment {

    private ImportOnlineWishlistFragment.OnFragmentInteractionListener mListener;

    private Button queryButton;

    private Button importButton;

    private EditText urlEditText;

    private LinearLayout resultBooksLinearLayout;

    private ProgressBar progressBar;

    private Map<Book, Boolean> resultBooks = new HashedMap<>();

    private Integer req = 0;

    private Integer res = 0;

    private Date startDate;

    public ImportOnlineWishlistFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ImportOnlineWishlistFragment.
     */
    public static ImportOnlineWishlistFragment newInstance() {
        ImportOnlineWishlistFragment fragment = new ImportOnlineWishlistFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.i("ImportOnlineWishlistFragment", "Created");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.information_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_info) {
            Bundle bundle = new Bundle();
            bundle.putString("element", "wishlist_layout");
            Navigation.findNavController(getActivity().findViewById(R.id.nav_host_fragment)).navigate(R.id.InformationFragment, bundle);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_import_online_wishlist, container, false);
        queryButton = view.findViewById(R.id.queryButton);
        importButton = view.findViewById(R.id.importButton);
        urlEditText = view.findViewById(R.id.urlEditText);
        resultBooksLinearLayout = view.findViewById(R.id.resultBooks);
        progressBar = view.findViewById(R.id.progressBar);

        queryButton.setOnClickListener(v -> {
            if (urlEditText.getText() != null) {

                try {
                    URI uri = URI.create(urlEditText.getText().toString());
                    String host = uri.getHost();

                    if (host == null) {
                        Snackbar.make(view, getContext().getString(R.string.wrong_url_error), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } else {
                        if (host.contains("bookline")) {
                            startQuery("bookline");
                        } else if (host.contains("moly")) {
                            startQuery("moly");
                        } else if (host.contains("libri")) {
                            startQuery("libri");
                        } else if (host.contains("21.szazadkiado")) {
                            startQuery("21.szazadkiado");
                        } else {
                            Snackbar.make(getActivity().getCurrentFocus(), getContext().getString(R.string.wrong_url_error), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        }
                    }
                } catch (IllegalArgumentException e) {
                    Snackbar.make(getActivity().getCurrentFocus(), getContext().getString(R.string.invalid_url_error), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            } else {
                Snackbar.make(getActivity().getCurrentFocus(), getContext().getString(R.string.invalid_url_error), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        importButton.setOnClickListener(v -> {

            List<Book> books = new ArrayList<>();
            for (Map.Entry<Book, Boolean> entry : resultBooks.entrySet()) {
                if (entry.getValue()) books.add(entry.getKey());
            }

            BookWishlist.mergeBooks(books);
            BookWishlist.saveListToSharedPreferences(getContext());
            Snackbar.make(getActivity().getCurrentFocus(), getContext().getString(R.string.import_finished), Snackbar.LENGTH_LONG).setAction("Action", null).show();
        });

        return view;
    }

    private void startQuery(String webpage) {

        startDate = new Date();
        resultBooks.clear();
        req = 0;
        res = 0;

        switch (webpage) {
            case "bookline":
                new BooklineWishlistParser(getContext(), listRequestHandler, bh, eh).start(urlEditText.getText().toString());
                break;
            case "libri":
                new LibriWishlistParser(getContext(), listRequestHandler, bh, eh).start(urlEditText.getText().toString());
                break;
            case "moly":
                new MolyWishlistParser(getContext(), listRequestHandler, bh, eh).start(urlEditText.getText().toString());
                break;
            case "21.szazadkiado":
                new Szazad21WishlistParser(getContext(), listRequestHandler, bh, eh).start(urlEditText.getText().toString());
                break;
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i("ImportOnlineWishlistFragment", "Attached");
        if (context instanceof ImportOnlineWishlistFragment.OnFragmentInteractionListener) {
            mListener = (ImportOnlineWishlistFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i("ImportOnlineWishlistFragment", "Detached");
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

    private BookHandler bh = new BookHandler() {
        @Override
        public void handleBook(Book b) {
            res++;
            progressBar.setProgress(res);
            if (b != null) {
                resultBooks.put(b, true);
            }
            Log.d("ImportOnlineWishlistFragment", "Requests: " + res + "/" + req);
            if (res.equals(req)) {
                onAllRequestComplete();
            }
        }
    };

    private ErrorHandler eh = new ErrorHandler() {
        @Override
        public void handleError(VolleyError error) {
            res++;
            progressBar.setProgress(res);
            if (res.equals(req)) {
                onAllRequestComplete();
            }
        }
    };

    private ListRequestHandler listRequestHandler = new ListRequestHandler() {
        @Override
        public void handleRequest(Integer bookCount) {
            if (bookCount > 0) {
                req = bookCount;
                progressBar.setMax(bookCount);
                progressBar.setVisibility(View.VISIBLE);
                Snackbar.make(getActivity().getCurrentFocus(), String.format(getContext().getString(R.string.importing_x_books), bookCount), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            } else {
                Snackbar.make(getActivity().getCurrentFocus(), getContext().getString(R.string.empty_wishlist_error), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }

        }
    };

    private void onAllRequestComplete() {
        Date endDate = new Date();
        long diff = (endDate.getTime() - startDate.getTime()) / 1000;
        Log.d("Request time", diff / 1000 + " seconds");
        Snackbar.make(getActivity().getCurrentFocus(), String.format(getContext().getString(R.string.query_finished_sec), diff), Snackbar.LENGTH_LONG).setAction("Action", null).show();

        if (resultBooks.size() > 0) {
            populateResultBooksLayout();
        } else {
            Snackbar.make(getActivity().getCurrentFocus(), getContext().getString(R.string.libriImportError), Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }

        progressBar.setVisibility(View.GONE);

    }

    private void populateResultBooksLayout() {
        resultBooksLinearLayout.removeAllViews();

        for (Map.Entry<Book, Boolean> entry : resultBooks.entrySet()) {
            Book b = entry.getKey();
            View view = getLayoutInflater().inflate(R.layout.book_import_item, resultBooksLinearLayout, false);
            ((TextView) view.findViewById(R.id.title)).setText(b.getTitle());
            ((TextView) view.findViewById(R.id.author)).setText(b.getAuthor());
            CheckBox cb = view.findViewById(R.id.checkBox);
            cb.setChecked(true);

            view.setOnClickListener(v -> {
                cb.setChecked(!cb.isChecked());
            });

            cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                /* String isbn = buttonView.getHint().toString();
                Optional<Book> optBook = resultBooks.keySet().stream().filter(book -> book.getISBN().equals(isbn)).findFirst();
                if (optBook.isPresent()) {
                    resultBooks.replace(optBook.get(), isChecked);
                }*/
                resultBooks.replace(b, isChecked);
            });

            resultBooksLinearLayout.addView(view);
        }

        if (resultBooks.size() > 0) {
            importButton.setVisibility(View.VISIBLE);
        }
    }

}
