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

import com.cyto.bargainbooks.R;
import com.cyto.bargainbooks.model.Book;

import androidx.navigation.Navigation;

public class AddBookFragment extends Fragment {

    private AddBookFragment.OnFragmentInteractionListener mListener;

    private EditText isbn;

    private EditText title;

    private EditText author;

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
        Log.i("AddBookFragment", "Created");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_book, container, false);

        isbn = view.findViewById(R.id.isbn);
        title = view.findViewById(R.id.title);
        author = view.findViewById(R.id.author);
        Button addBook = view.findViewById(R.id.add_book);

        addBook.setOnClickListener(v -> {

            Book b = new Book();
            b.setISBN(isbn.getText().toString());
            b.setAuthor(author.getText().toString());
            b.setTitle(title.getText().toString());

            Snackbar.make(view, "Added!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            Navigation.findNavController(getActivity().findViewById(R.id.nav_host_fragment)).popBackStack();
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
        Log.i("AddBookFragment", "Attached");
        if (context instanceof AddBookFragment.OnFragmentInteractionListener) {
            mListener = (AddBookFragment.OnFragmentInteractionListener) context;
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
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}
