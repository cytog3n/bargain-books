package com.cyto.bargainbooks.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.design.widget.NavigationView;
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
import android.widget.TextView;

import androidx.navigation.Navigation;

import com.cyto.bargainbooks.R;
import com.cyto.bargainbooks.model.Book;
import com.cyto.bargainbooks.parser.MyLibraryParser;
import com.cyto.bargainbooks.storage.BookWishlist;

import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.util.List;

public class ImportBooksFragment extends Fragment {

    private static final int READ_REQUEST_CODE = 42;
    private ImportBooksFragment.OnFragmentInteractionListener mListener;
    private TextView browsePlaceholder;
    private Uri uri;

    public ImportBooksFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ImportBooksFragment.
     */
    public static ImportBooksFragment newInstance() {
        ImportBooksFragment fragment = new ImportBooksFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i("ImportBooksFragment", "Created");
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
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
            bundle.putString("element", "mylibrary_wishlist_layout");
            Navigation.findNavController(getActivity().findViewById(R.id.nav_host_fragment)).navigate(R.id.InformationFragment, bundle);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_import_books, container, false);
        ((NavigationView) getActivity().findViewById(R.id.nav_view)).setCheckedItem(R.id.nav_import_books);

        Button send = view.findViewById(R.id.send);
        Button browse = view.findViewById(R.id.browse);
        browsePlaceholder = view.findViewById(R.id.browse_placeholder);

        browse.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/vnd.ms-excel");
            startActivityForResult(intent, READ_REQUEST_CODE);
        });

        send.setOnClickListener(v -> {

            if (uri == null) {
                Snackbar.make(view, "Select a file first", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            } else {
                try {
                    ParcelFileDescriptor pfd = getActivity().getContentResolver().openFileDescriptor(uri, "r");
                    FileInputStream fis = new FileInputStream(pfd.getFileDescriptor());
                    MyLibraryParser mlp = new MyLibraryParser();
                    List<Book> books = mlp.getBooks(fis);
                    BookWishlist.saveBooks(books);
                    BookWishlist.saveListToSharedPreferences(getContext());
                    fis.close();
                    pfd.close();

                    Snackbar.make(view, "Done", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    Navigation.findNavController(getActivity().findViewById(R.id.nav_host_fragment)).navigate(R.id.WishlistFragment);
                    NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
                    navigationView.setCheckedItem(R.id.nav_wishlist);
                } catch (Exception e) {
                    Log.d("Import MyLibrary", e.getMessage());
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                String[] s = uri.getPath().split("/");
                browsePlaceholder.setText(s[s.length - 1]);
                Log.d("File selected: ", uri.getPath());
            }
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
        Log.i("ImportBooksFragment", "Attached");
        if (context instanceof ImportBooksFragment.OnFragmentInteractionListener) {
            mListener = (ImportBooksFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i("ImportBooksFragment", "Detached");
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
