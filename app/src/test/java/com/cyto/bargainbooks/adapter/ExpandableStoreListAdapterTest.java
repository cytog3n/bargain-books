package com.cyto.bargainbooks.adapter;

import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import com.cyto.bargainbooks.MainActivity;
import com.cyto.bargainbooks.R;
import com.cyto.bargainbooks.config.Constants;
import com.cyto.bargainbooks.model.Book;

import org.apache.commons.collections4.map.HashedMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
public class ExpandableStoreListAdapterTest {

    ExpandableStoreListAdapter expandableStoreListAdapter;

    MainActivity myActivity;

    @Before
    public void setUp() throws Exception {

        Book book1 = new Book();
        book1.setISBN("123456789");
        book1.setAuthor("Author1");
        book1.setTitle("Title1");
        book1.setSalePercent(75L);

        Book book2 = new Book();
        book2.setISBN("123456798");
        book2.setAuthor("Author2");
        book2.setTitle("Title2");
        book2.setSalePercent(50L);

        List<Pair> listHead = new ArrayList<>();
        listHead.add(new Pair("libri", 1));
        listHead.add(new Pair("bookline", 2));

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

        Map<String, List<Book>> details = new HashedMap<>();
        List<Book> b1 = new ArrayList<>();
        b1.add(book1Libri);

        List<Book> b2 = new ArrayList<>();
        b2.add(book1Bookline);
        b2.add(book2Bookline);

        details.put("libri", b1);
        details.put("bookline", b2);

        myActivity = Robolectric.buildActivity(MainActivity.class).create().get();

        expandableStoreListAdapter = new ExpandableStoreListAdapter(ApplicationProvider.getApplicationContext(), myActivity, listHead, details);
    }

    @Test
    public void getChild() {
        Book b = (Book) expandableStoreListAdapter.getChild(0, 0);
        assertEquals("It should be the title of the first book", "Title1", b.getTitle());
        assertEquals("It should be the author of the first book", "Author1", b.getAuthor());
        assertEquals("It should be the ISBN of the first book", "123456789", b.getISBN());
        assertEquals("It should return the store of the first child", "libri", b.getStore());
    }

    @Test
    public void getChildId() {
        assertEquals("It should return the second parameter, there is no need to use hascode for ID", 1, expandableStoreListAdapter.getChildId(0, 1));
    }

    @Test
    public void getChildView() {
        View view = expandableStoreListAdapter.getChildView(0, 0, true, null, null);
        assertNotNull(view);
        TextView bookName = view.findViewById(R.id.bookName);
        assertNotNull(bookName);
        TextView price = view.findViewById(R.id.price);
        assertNotNull(price);
        TextView off = view.findViewById(R.id.off);
        assertNotNull(off);
        String priceResource = ApplicationProvider.getApplicationContext().getString(R.string.price_tag_huf, 1000L);
        String offResource = ApplicationProvider.getApplicationContext().getString(R.string.sale_percent_parenthesis, 50L);

        assertEquals("The first book from libri", "Title1", bookName.getText());
        assertEquals("This first book from libri", priceResource, price.getText());
        assertEquals("This first book from libri", offResource, off.getText());
    }

    @Test
    public void getChildrenCount() {
        assertEquals("The first one have 1 elements", 1, expandableStoreListAdapter.getChildrenCount(0));
        assertEquals("The second one have 2 element", 2, expandableStoreListAdapter.getChildrenCount(1));
    }

    @Test
    public void getGroup() {
        Pair<String, Integer> head = (Pair<String, Integer>) expandableStoreListAdapter.getGroup(0);

        assertEquals("This should return the storeName", "libri", head.first);
        assertEquals("This should return the sales of the first store", 1, (int) head.second);
    }

    @Test
    public void getGroupCount() {
        assertEquals("There are 2 different books", 2, expandableStoreListAdapter.getGroupCount());
    }

    @Test
    public void getGroupId() {
        assertEquals("This should return the index of the group", 0, expandableStoreListAdapter.getGroupId(0));
    }

    @Test
    public void getGroupView() {

        View view = expandableStoreListAdapter.getGroupView(0, false, null, null);
        assertNotNull(view);
        TextView storeName = view.findViewById(R.id.store_name);
        assertNotNull(storeName);
        TextView saleCount = view.findViewById(R.id.sale_count);
        assertNotNull(saleCount);

        assertEquals("The storeName should come from the StoreMap", Constants.storeMap.get("libri"), storeName.getText());
        assertEquals("There is 1 item in the first group", "1", saleCount.getText());
    }

    @Test
    public void hasStableIds() {
        assertEquals("This is false, because we use the the index as ID", false, expandableStoreListAdapter.hasStableIds());
    }

    @Test
    public void isChildSelectable() {
        assertEquals("All the childs should be selectable", true, expandableStoreListAdapter.isChildSelectable(0, 0));
    }
}