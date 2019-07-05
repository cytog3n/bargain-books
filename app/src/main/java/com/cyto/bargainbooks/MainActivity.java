package com.cyto.bargainbooks;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import androidx.navigation.Navigation;

import com.cyto.bargainbooks.fragment.AddBookFragment;
import com.cyto.bargainbooks.fragment.BookDetailEditFragment;
import com.cyto.bargainbooks.fragment.BookDetailFragment;
import com.cyto.bargainbooks.fragment.ConfigFragment;
import com.cyto.bargainbooks.fragment.ImportBooksFragment;
import com.cyto.bargainbooks.fragment.ImportOnlineWishlistFragment;
import com.cyto.bargainbooks.fragment.InformationFragment;
import com.cyto.bargainbooks.fragment.SaleFragment;
import com.cyto.bargainbooks.fragment.WishlistFragment;
import com.cyto.bargainbooks.service.VolleyService;
import com.cyto.bargainbooks.storage.BookSaleList;
import com.cyto.bargainbooks.storage.BookWishlist;
import com.cyto.bargainbooks.storage.Config;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, WishlistFragment.OnFragmentInteractionListener,
        SaleFragment.OnFragmentInteractionListener, ImportBooksFragment.OnFragmentInteractionListener, ConfigFragment.OnFragmentInteractionListener,
        BookDetailFragment.OnFragmentInteractionListener, ImportOnlineWishlistFragment.OnFragmentInteractionListener,
        BookDetailEditFragment.OnFragmentInteractionListener, AddBookFragment.OnFragmentInteractionListener, InformationFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        BookWishlist.initializeList(this);
        BookSaleList.initializeList(this);
        Config.getInstance(this);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_wishlist);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            VolleyService vs = VolleyService.getInstance(this);
            if (vs.isThereAnyActiveRequests()) {
                new AlertDialog.Builder(this).setTitle(getString(R.string.clear_request_queue_confirm_title))
                        .setMessage(getString(R.string.clear_request_queue_confirm_text))
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity.super.onBackPressed();
                            }
                        })
                        .setNegativeButton(getString(R.string.no), null)
                        .show();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        VolleyService vs = VolleyService.getInstance(this);

        if (vs.isThereAnyActiveRequests()) {
            new AlertDialog.Builder(this).setTitle(getString(R.string.clear_request_queue_confirm_title))
                    .setMessage(getString(R.string.clear_request_queue_confirm_text))
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            vs.clearQueue();
                            navigate(id);
                        }
                    })
                    .setNegativeButton(getString(R.string.no), null)
                    .show();
        } else {
            navigate(id);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.d("Fragment interaction: ", uri.toString());
    }

    private void navigate(int id) {

        if (id == R.id.nav_wishlist) {
            Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(R.id.WishlistFragment);
        } else if (id == R.id.nav_sale) {
            Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(R.id.SaleFragment);
        } else if (id == R.id.nav_import_books) {
            Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(R.id.ImportBooksFragment);
        } else if (id == R.id.nav_config) {
            Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(R.id.ConfigFragment);
        } else if (id == R.id.nav_import_libri_books) {
            Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(R.id.ImportOnlineWishListFragment);
        } else if (id == R.id.nav_info) {
            Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(R.id.InformationFragment);
        }

    }

}
