package com.cyto.bargainbooks.adapter;

import android.util.Pair;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import com.cyto.bargainbooks.R;
import com.cyto.bargainbooks.model.Book;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
public class ExpandableBookListAdapterTest {

    ExpandableBookListAdapter expandableBookListAdapter;

    @Before
    public void setUp() throws Exception {

        Book book1 = new Book();
        book1.setISBN("123456789");
        book1.setAuthor("Author1");
        book1.setTitle("Title1");
        book1.setSalePercent(75L);
        book1.setOriginalPrice(2000L);

        Book book2 = new Book();
        book2.setISBN("123456798");
        book2.setAuthor("Author2");
        book2.setTitle("Title2");
        book2.setSalePercent(50L);
        book2.setOriginalPrice(3000L);

        List<Pair> listTitle = new ArrayList<>();
        listTitle.add(new Pair(book1.getISBN(), book1));
        listTitle.add(new Pair(book2.getISBN(), book2));

        Book book1Libri = new Book();
        book1Libri.setISBN(book1.getISBN());
        book1Libri.setTitle(book1.getTitle());
        book1Libri.setAuthor(book1.getAuthor());
        book1Libri.setStore("libri");
        book1Libri.setOriginalPrice(2000L);
        book1Libri.setNewPrice(1000L);
        book1Libri.setSalePercent(50L);
        book1Libri.setUrl("https://libri.hu/test");

        Book book1Bookline = new Book();
        book1Bookline.setISBN(book1.getISBN());
        book1Bookline.setTitle(book1.getTitle());
        book1Bookline.setAuthor(book1.getAuthor());
        book1Bookline.setStore("bookline");
        book1Bookline.setOriginalPrice(2000L);
        book1Bookline.setNewPrice(500L);
        book1Bookline.setSalePercent(75L);
        book1Bookline.setUrl("https://bookline.hu/test");

        Book book2Bookline = new Book();
        book2Bookline.setISBN(book2.getISBN());
        book2Bookline.setTitle(book2.getTitle());
        book2Bookline.setAuthor(book2.getAuthor());
        book2Bookline.setStore("bookline");
        book2Bookline.setOriginalPrice(3000L);
        book2Bookline.setNewPrice(1500L);
        book2Bookline.setSalePercent(50L);
        book2Bookline.setUrl("https://bookline.hu/test2");

        Map<String, List<Book>> listDetail = new HashMap<>();
        List<Book> b1 = new ArrayList<>();
        b1.add(book1Libri);
        b1.add(book1Bookline);

        List<Book> b2 = new ArrayList<>();
        b2.add(book2Bookline);

        listDetail.put(book1.getISBN(), b1);
        listDetail.put(book2.getISBN(), b2);

        expandableBookListAdapter = new ExpandableBookListAdapter(ApplicationProvider.getApplicationContext(), listTitle, listDetail);
    }

    @Test
    public void getChild() {
        Book b = (Book) expandableBookListAdapter.getChild(0, 0);
        assertEquals("The title of the first book", "Title1", b.getTitle());
        assertEquals("The store of the first book", "libri", b.getStore());
        assertEquals("The price should be 1000", 1000L, (long) b.getNewPrice());
        assertEquals("The off should be 50", 50L, (long) b.getSalePercent());
    }

    @Test
    public void getChildId() {
        assertEquals("It should return the second parameter, there is no need to use hascode for ID", 1, expandableBookListAdapter.getChildId(0, 1));
    }

    @Test
    public void getChildView() {

        View view = expandableBookListAdapter.getChildView(0, 0, false, null, null);
        assertNotNull(view);

        TextView shopName = view.findViewById(R.id.shopName);
        TextView price = view.findViewById(R.id.price);
        TextView off = view.findViewById(R.id.off);

        assertNotNull(off);
        assertNotNull(price);
        assertNotNull(shopName);

        String priceResource = ApplicationProvider.getApplicationContext().getString(R.string.price_tag_huf, 1000L);
        String offResource = ApplicationProvider.getApplicationContext().getString(R.string.sale_percent_parenthesis, 50L);

        assertEquals(priceResource, price.getText());
        assertEquals(offResource, off.getText());
        assertEquals("Libri", shopName.getText());

    }

    @Test
    public void getChildrenCount() {
        assertEquals("There is 2 books for the first ISBN", 2, expandableBookListAdapter.getChildrenCount(0));
    }

    @Test
    public void getGroup() {
        Pair<String, Book> p = (Pair) expandableBookListAdapter.getGroup(0);
        assertEquals("The isbn should be the first book", "123456789", p.first);

        Book b = p.second;
        assertEquals("Title1", b.getTitle());
        assertEquals("Author1", b.getAuthor());
        assertEquals("123456789", b.getISBN());
    }

    @Test
    public void getGroupCount() {
        assertEquals("There is 2 different books in the list", 2, expandableBookListAdapter.getGroupCount());
    }

    @Test
    public void getGroupId() {
        assertEquals("The index is used for ID", 0, expandableBookListAdapter.getGroupId(0));
    }

    @Test
    public void getGroupView() {
        View view = expandableBookListAdapter.getGroupView(0, false, null, null);
        assertNotNull(view);

        TextView title = view.findViewById(R.id.title);
        assertNotNull(title);

        TextView off = view.findViewById(R.id.off);
        assertNotNull(off);

        TextView author = view.findViewById(R.id.author);
        assertNotNull(author);

        LinearLayout originalPriceParent = view.findViewById(R.id.original_price_parent);
        assertNotNull(originalPriceParent);

        assertEquals("The title of the first book", "Title1", title.getText());
        assertEquals("The author of the first book", "Author1", author.getText());

        String offResource = ApplicationProvider.getApplicationContext().getString(R.string.sale_percent, 75L);
        assertEquals("The best discount is 75%", offResource, off.getText());
        assertEquals("While the group is not expanded, the original price should be hidden", View.GONE, originalPriceParent.getVisibility());

        view = expandableBookListAdapter.getGroupView(0, true, null, null);
        originalPriceParent = view.findViewById(R.id.original_price_parent);
        assertNotNull(originalPriceParent);
        assertEquals("While the group is expanded, the original price should be visible", View.VISIBLE, originalPriceParent.getVisibility());
    }

    @Test
    public void hasStableIds() {
        assertEquals("This is false, because we use the the index as ID", false, expandableBookListAdapter.hasStableIds());
    }

    @Test
    public void isChildSelectable() {
        assertEquals("All child should be selectable", true, expandableBookListAdapter.isChildSelectable(0, 0));
    }
}
