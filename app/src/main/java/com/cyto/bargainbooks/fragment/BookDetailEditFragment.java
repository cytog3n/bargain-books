package com.cyto.bargainbooks.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.navigation.Navigation;

import com.cyto.bargainbooks.R;
import com.cyto.bargainbooks.model.Book;
import com.cyto.bargainbooks.storage.BookWishlist;

import java.util.Optional;

public class BookDetailEditFragment extends Fragment {

    EditText title;
    EditText author;
    EditText isbn;
    private OnFragmentInteractionListener mListener;
    private Book book;

    public BookDetailEditFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BookDetailFragment.
     */
    public static BookDetailEditFragment newInstance() {
        BookDetailEditFragment fragment = new BookDetailEditFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("BookDetailEditFragment", "Created");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_edit_detail, container, false);
        ((NavigationView) getActivity().findViewById(R.id.nav_view)).setCheckedItem(R.id.nav_wishlist);

        TextView titleLabel = view.findViewById(R.id.title_label);
        TextView authorLabel = view.findViewById(R.id.author_label);
        TextView isbnLabel = view.findViewById(R.id.isbn_value);

        title = view.findViewById(R.id.title);
        author = view.findViewById(R.id.author);
        isbn = view.findViewById(R.id.isbn);

        Button saveBtn = view.findViewById(R.id.save);

        LinearLayout stores = view.findViewById(R.id.stores);
        String bisbn = this.getArguments().getString("isbn");
        Optional<Book> optBook = BookWishlist.getBooks().stream().filter(book1 -> book1.getISBN().equals(bisbn)).findFirst();

        if (optBook.isPresent()) {
            book = optBook.get();
            titleLabel.setText(book.getTitle());
            authorLabel.setText(book.getAuthor());
            isbnLabel.setText(book.getISBN());

            title.setText(book.getTitle());
            author.setText(book.getAuthor());
            isbn.setText(book.getISBN());
        }

        saveBtn.setOnClickListener(v -> {

            if (bookDetailsValid()) {

                book.setTitle(title.getText().toString());
                book.setAuthor(author.getText().toString());
                book.setISBN(isbn.getText().toString());

                BookWishlist.saveListToSharedPreferences(getContext());

                Navigation.findNavController(getActivity().findViewById(R.id.nav_host_fragment)).navigateUp();
                Snackbar.make(getActivity().getCurrentFocus(), getContext().getText(R.string.book_detail_updated), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            } else {
                Snackbar.make(getActivity().getCurrentFocus(), getContext().getText(R.string.book_wrong_detail), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        return view;
    }

    private boolean bookDetailsValid() {
        if (title.getText().toString().trim().equals("")) return false;
        if (author.getText().toString().trim().equals("")) return false;
        if (isbn.getText().toString().length() != 13 && isbn.getText().toString().length() != 9) {
            return false;
        }
        return isbn.getText().toString().matches("\\d+");
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i("BookDetailEditFragment", "Attached");
        if (context instanceof BookDetailEditFragment.OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i("BookDetailEditFragment", "Detached");
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

}
