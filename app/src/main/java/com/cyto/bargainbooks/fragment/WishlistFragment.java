package com.cyto.bargainbooks.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
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

import androidx.navigation.Navigation;

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

            new AlertDialog.Builder(getContext()).setTitle(getString(R.string.clear_confirm_title))
                    .setMessage(getString(R.string.clear_confirm_text))
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            BookWishlist.clear();
                            BookWishlist.saveListToSharedPreferences(getContext());
                            listTitle.clear();
                            refreshFragment();
                        }
                    })
                    .setNegativeButton(getString(R.string.no), null)
                    .show();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wishlist, container, false);
        ((NavigationView) getActivity().findViewById(R.id.nav_view)).setCheckedItem(R.id.nav_wishlist);
        listTitle.clear();

        ListView listView = view.findViewById(R.id.listView);

        List<Book> books = BookWishlist.getBooks();
        Set<String> isbns = new HashSet<>();

        for (Book book : books) {
            String isbn = book.getISBN();
            if (!isbns.contains(isbn)) {
                listTitle.add(new Pair(isbn, book));
                isbns.add(isbn);
            }
        }

        WishlistListAdapter wishlistListAdapter = new WishlistListAdapter(getContext(), listTitle);
        wishlistListAdapter.notifyDataSetChanged();
        listView.setAdapter(wishlistListAdapter);

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Book b = books.get(position);
            Bundle bundle = new Bundle();
            bundle.putString("isbn", b.getISBN());
            Navigation.findNavController(getActivity().findViewById(R.id.nav_host_fragment)).navigate(R.id.BookDetailFragment, bundle);
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

    /**
     * Refreshes the fragment, so the onCreateView method will initialize the whole fragment.
     */
    private void refreshFragment() {
        if (getFragmentManager().getPrimaryNavigationFragment() == this) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (Build.VERSION.SDK_INT >= 26) {
                ft.setReorderingAllowed(false);
            }
            ft.detach(this).attach(this).commit();
        }
    }

}
