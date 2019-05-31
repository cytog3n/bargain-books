package com.cyto.bargainbooks;

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

import com.cyto.bargainbooks.config.Config;
import com.cyto.bargainbooks.fragment.AddBookFragment;
import com.cyto.bargainbooks.fragment.ConfigFragment;
import com.cyto.bargainbooks.fragment.ImportBooksFragment;
import com.cyto.bargainbooks.fragment.SaleFragment;
import com.cyto.bargainbooks.fragment.SearchFragment;
import com.cyto.bargainbooks.fragment.WishlistFragment;
import com.cyto.bargainbooks.model.Book;
import com.cyto.bargainbooks.storage.BookSaleList;
import com.cyto.bargainbooks.storage.BookWishlist;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, WishlistFragment.OnFragmentInteractionListener,
        SearchFragment.OnFragmentInteractionListener, AddBookFragment.OnFragmentInteractionListener, SaleFragment.OnFragmentInteractionListener, ImportBooksFragment.OnFragmentInteractionListener,
        ConfigFragment.OnFragmentInteractionListener {

    private Book testBook = null;

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
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_search) {
            Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(R.id.SearchFragment);
        } else if (id == R.id.nav_wishlist) {
            Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(R.id.WishlistFragment);
        } else if (id == R.id.nav_sale) {
            Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(R.id.SaleFragment);
        } else if (id == R.id.nav_import_books) {
            Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(R.id.ImportBooksFragment);
        } else if (id == R.id.nav_config) {
            Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(R.id.ConfigFragment);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.d("Fragment interaction: ", uri.toString());
    }

}
