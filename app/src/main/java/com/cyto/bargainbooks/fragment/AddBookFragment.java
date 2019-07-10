package com.cyto.bargainbooks.fragment;

import android.content.Context;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.navigation.Navigation;

import com.cyto.bargainbooks.R;
import com.cyto.bargainbooks.factory.book.BookFactory;
import com.cyto.bargainbooks.factory.book.BooklineBookFactory;
import com.cyto.bargainbooks.factory.book.LibriBookFactory;
import com.cyto.bargainbooks.factory.book.MolyBookFactory;
import com.cyto.bargainbooks.factory.book.Szazad21BookFactory;
import com.cyto.bargainbooks.factory.request.BookDetailRequestFactory;
import com.cyto.bargainbooks.model.Book;
import com.cyto.bargainbooks.request.handler.BookHandler;
import com.cyto.bargainbooks.service.VolleyService;
import com.cyto.bargainbooks.storage.BookWishlist;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

public class AddBookFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private EditText url;

    private Button queryBtn;

    private Button importBtn;

    private TextView author;

    private TextView title;

    private TextView isbn;

    private LinearLayout resultLayout;

    private Book book;

    private List<String> storeList = Arrays.asList("bookline", "libri", "moly", "21.szazadkiado");

    private BookDetailRequestFactory bookDetailRequestFactory;

    private VolleyService vs;

    public AddBookFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AddBookFragment.
     */
    public static AddBookFragment newInstance() {
        AddBookFragment fragment = new AddBookFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.i("AddBookFragment", "Created");
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
            bundle.putString("element", "add_book_layout");
            Navigation.findNavController(getActivity().findViewById(R.id.nav_host_fragment)).navigate(R.id.InformationFragment, bundle);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_book, container, false);

        url = view.findViewById(R.id.url);
        queryBtn = view.findViewById(R.id.queryBtn);
        importBtn = view.findViewById(R.id.importBtn);
        author = view.findViewById(R.id.author);
        title = view.findViewById(R.id.title);
        isbn = view.findViewById(R.id.isbn_value);
        resultLayout = view.findViewById(R.id.resultLayout);

        vs = VolleyService.getInstance(getContext());

        queryBtn.setOnClickListener(v -> {
            BookFactory bf = null;
            switch (getStore()) {
                case "bookline":
                    bf = new BooklineBookFactory();
                    break;
                case "libri":
                    bf = new LibriBookFactory();
                    break;
                case "moly":
                    bf = new MolyBookFactory();
                    break;
                case "21.szazadkiado":
                    bf = new Szazad21BookFactory();
                    break;
                default:
                    Snackbar.make(getActivity().getCurrentFocus(), getContext().getText(R.string.wrong_url_error), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    break;
            }

            if (bf != null) {
                vs.addToRequestQueue(bookDetailRequestFactory.getStringRequest(url.getText().toString(), bh, null, bf));
            }
        });

        importBtn.setOnClickListener(v -> {
            BookWishlist.mergeBooks(Arrays.asList(book));
            BookWishlist.saveListToSharedPreferences(getContext());
            Snackbar.make(getActivity().getCurrentFocus(), getContext().getText(R.string.import_finished), Snackbar.LENGTH_LONG).setAction("Action", null).show();

            Navigation.findNavController(getActivity().findViewById(R.id.nav_host_fragment)).getCurrentDestination();
            Navigation.findNavController(getActivity().findViewById(R.id.nav_host_fragment)).navigateUp();

        });

        return view;
    }

    private String getStore() {
        String uri = url.getText().toString();
        String correctLink = "error";

        for (String s : storeList) {
            if (uri.contains(s)) {
                correctLink = s;
                break;
            }
        }

        try {
            URI u = new URI(uri);
        } catch (URISyntaxException e) {
            correctLink = "error";
        }

        return correctLink;
    }

    private BookHandler bh = new BookHandler() {
        @Override
        public void handleBook(Book b) {
            if (b == null) {
                Snackbar.make(getActivity().getCurrentFocus(), getContext().getText(R.string.wrong_url_error), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            } else {
                book = b;

                title.setText(b.getTitle());
                author.setText(b.getAuthor());
                isbn.setText(b.getISBN());

                resultLayout.setVisibility(View.VISIBLE);
            }
        }
    };

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i("AddBookFragment", "Attached");
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i("AddBookFragment", "Detached");
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href="http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
