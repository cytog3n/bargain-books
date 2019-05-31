package com.cyto.bargainbooks.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.ListView;

import com.cyto.bargainbooks.R;
import com.cyto.bargainbooks.adapter.WishlistListAdapter;
import com.cyto.bargainbooks.model.Book;
import com.cyto.bargainbooks.storage.BookWishlist;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WishlistFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private final List<Pair> listTitle = new ArrayList<>();

    public WishlistFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment WishlistFragment.
     */
    public static WishlistFragment newInstance() {
        WishlistFragment fragment = new WishlistFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.i("WishlistFragment", "Created");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.wishlist_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_clear_wishlist) {
            BookWishlist.clear();
            BookWishlist.saveListToSharedPreferences(getContext());
            listTitle.clear();
            refreshFragment();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wishlist, container, false);

        ListView listView = view.findViewById(R.id.listView);
        Context context = getActivity().getApplicationContext();

        List<Book> books = BookWishlist.getBooks();
        Set<String> isbns = new HashSet<>();

        for (Book book : books) {
            String isbn = book.getISBN();
            if (!isbns.contains(isbn)) {
                listTitle.add(new Pair(isbn, book));
                isbns.add(isbn);
            }
        }

        WishlistListAdapter wishlistListAdapter = new WishlistListAdapter(context, listTitle);
        wishlistListAdapter.notifyDataSetChanged();
        listView.setAdapter(wishlistListAdapter);

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
        Log.i("WishlistFragment", "Attached");
        if (context instanceof SearchFragment.OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i("WishlistFragment", "Detached");
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
        ft.detach(this).attach(this).commit();
    }

}
