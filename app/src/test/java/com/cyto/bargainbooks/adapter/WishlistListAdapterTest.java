package com.cyto.bargainbooks.adapter;

import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import com.cyto.bargainbooks.R;
import com.cyto.bargainbooks.model.Book;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
public class WishlistListAdapterTest {

    private WishlistListAdapter wishlistListAdapter;

    private List<Pair> listTitle;

    @Before
    public void setUp() throws Exception {
        Book b1 = new Book();
        b1.setAuthor("Author1");
        b1.setTitle("Title1");
        b1.setISBN("123456789");

        Book b2 = new Book();
        b2.setAuthor("Author2");
        b2.setTitle("Title2");
        b2.setISBN("123456798");

        listTitle = new ArrayList<>();
        listTitle.add(new Pair(b1.getISBN(), b1));
        listTitle.add(new Pair(b2.getISBN(), b2));

        wishlistListAdapter = new WishlistListAdapter(ApplicationProvider.getApplicationContext(), listTitle);
    }

    @Test
    public void getCount() {
        assertEquals(2, wishlistListAdapter.getCount());
    }

    @Test
    public void getItem() {
        Book b = (Book) ((Pair) wishlistListAdapter.getItem(0)).second;
        assertEquals("Author1", b.getAuthor());
        assertEquals("Title1", b.getTitle());
        assertEquals("123456789", b.getISBN());
    }

    @Test
    public void getItemId() {
        Assert.assertEquals("123456789".hashCode(), wishlistListAdapter.getItemId(0));
    }

    @Test
    public void getView() {
        View view = wishlistListAdapter.getView(0, null, null);
        assertNotNull(view);

        TextView title = view.findViewById(R.id.title);
        assertNotNull(title);
        assertEquals("Title1", title.getText());

        TextView author = view.findViewById(R.id.author);
        assertNotNull(author);
        assertEquals("Author1", author.getText());
    }
}