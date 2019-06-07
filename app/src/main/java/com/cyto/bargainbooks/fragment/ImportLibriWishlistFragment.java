package com.cyto.bargainbooks.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.cyto.bargainbooks.R;
import com.cyto.bargainbooks.model.Book;
import com.cyto.bargainbooks.parser.LibriWishlistParser;
import com.cyto.bargainbooks.request.handler.BookHandler;
import com.cyto.bargainbooks.request.handler.ErrorHandler;
import com.cyto.bargainbooks.request.handler.ListRequestHandler;
import com.cyto.bargainbooks.storage.BookWishlist;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ImportLibriWishlistFragment extends Fragment {

    private ImportLibriWishlistFragment.OnFragmentInteractionListener mListener;

    private View view;

    private Button queryButton;

    private Button importButton;

    private EditText urlEditText;

    private LinearLayout resultBooksLinearLayout;

    private ProgressBar progressBar;

    private List<Book> resultBooks = new ArrayList<>();

    private Integer req = 0;

    private Integer res = 0;

    private Date startDate;

    public ImportLibriWishlistFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ImportLibriWishlistFragment.
     */
    public static ImportLibriWishlistFragment newInstance() {
        ImportLibriWishlistFragment fragment = new ImportLibriWishlistFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("ImportLibriWishlistFragment", "Created");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_import_libri_wishlist, container, false);
        queryButton = view.findViewById(R.id.queryButton);
        importButton = view.findViewById(R.id.importButton);
        urlEditText = view.findViewById(R.id.urlEditText);
        resultBooksLinearLayout = view.findViewById(R.id.resultBooks);
        progressBar = view.findViewById(R.id.progressBar);

        queryButton.setOnClickListener(v -> {
            if (urlEditText.getText() != null) {
                startDate = new Date();
                resultBooks.clear();
                req = 0;
                res = 0;
                new LibriWishlistParser(getContext(), listRequestHandler, bh, eh).start(urlEditText.getText().toString());
            } else {
                Snackbar.make(view, getContext().getString(R.string.empty_url_error), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        importButton.setOnClickListener(v -> {
            BookWishlist.saveBooks(resultBooks);
            BookWishlist.saveListToSharedPreferences(getContext());
            Snackbar.make(view, getContext().getString(R.string.import_finished), Snackbar.LENGTH_LONG).setAction("Action", null).show();
        });

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
        Log.i("ImportLibriWishlistFragment", "Attached");
        if (context instanceof ImportLibriWishlistFragment.OnFragmentInteractionListener) {
            mListener = (ImportLibriWishlistFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i("ImportLibriWishlistFragment", "Detached");
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
                resultBooks.add(b);
            }
            Log.d("ImportLibriWishlistFragment", "Requests: " + res + "/" + req);
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
                Snackbar.make(view, String.format(getContext().getString(R.string.importing_x_books), bookCount), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            } else {
                Snackbar.make(view, getContext().getString(R.string.empty_url_error), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }

        }
    };

    private void onAllRequestComplete() {
        Date endDate = new Date();
        long diff = (endDate.getTime() - startDate.getTime()) / 1000;
        Log.d("Request time", diff / 1000 + " seconds");
        Snackbar.make(view, String.format(getContext().getString(R.string.query_finished_sec), diff), Snackbar.LENGTH_LONG).setAction("Action", null).show();

        if (resultBooks.size() > 0) {
            populateResultBooksLayout();
        } else {
            Snackbar.make(view, getContext().getString(R.string.libriImportError), Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }

        progressBar.setVisibility(View.GONE);

    }

    private void populateResultBooksLayout() {
        resultBooksLinearLayout.removeAllViews();
        for (Book b : resultBooks) {
            View view = getLayoutInflater().inflate(R.layout.book_import_item, resultBooksLinearLayout, false);
            ((TextView) view.findViewById(R.id.title)).setText(b.getTitle());
            ((TextView) view.findViewById(R.id.author)).setText(b.getAuthor());
            resultBooksLinearLayout.addView(view);
        }

        if (resultBooks.size() > 0) {
            importButton.setVisibility(View.VISIBLE);
        }
    }

}
