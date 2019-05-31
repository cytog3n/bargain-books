package com.cyto.bargainbooks.adapter;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.cyto.bargainbooks.R;
import com.cyto.bargainbooks.config.Constant;
import com.cyto.bargainbooks.model.Book;

import java.util.List;
import java.util.Map;

public class ExpandableStoreListAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final List<Pair> expandableListTitle;
    private final Map<String, List<Book>> expandableListDetail;

    public ExpandableStoreListAdapter(Context context, List<Pair> expandableListTitle, Map<String, List<Book>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition).first).get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final Book book = (Book) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.store_level_list_item, null);
        }

        TextView bookName = convertView.findViewById(R.id.bookName);
        if (book.getTitle() == null) {
            bookName.setText(context.getString(R.string.no_available_store));
        } else {
            bookName.setText(book.getTitle());
        }

        TextView off = convertView.findViewById(R.id.off);
        if (book.getSalePercent() == null) {
            off.setText("");
        } else {
            off.setText("(" + book.getSalePercent() + "%)");
        }

        TextView price = convertView.findViewById(R.id.price);
        if (book.getNewPrice() == null) {
            price.setText("");
        } else {
            price.setText(book.getNewPrice() + " Ft");
        }

        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition).first).size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.store_level_list_group, null);
        }

        Integer count = (Integer) ((Pair) getGroup(listPosition)).second;
        String shopName = (String) ((Pair) getGroup(listPosition)).first;
        shopName = Constant.storeMap.get(shopName);

        TextView shopNameTextView = convertView.findViewById(R.id.store_name);
        shopNameTextView.setText(shopName);

        TextView countTextView = convertView.findViewById(R.id.sale_count);
        countTextView.setText(count.toString());

        /* String title = b.getTitle();
        String author = b.getAuthor();
        String off = b.getSalePercent() + "%";
        String originalPrice = String.valueOf(b.getOriginalPrice());

        TextView titleTextView = convertView.findViewById(R.id.title);
        titleTextView.setText(title);

        TextView authorTextView = convertView.findViewById(R.id.author);
        authorTextView.setText(author);

        TextView saleTextView = convertView.findViewById(R.id.off);
        if (b.getSalePercent() == null) {
            saleTextView.setText("");
        } else {
            saleTextView.setText(off);
        }

        TextView originalPriceTextView = convertView.findViewById(R.id.original_price_value);
        if (b.getOriginalPrice() == null) {
             originalPriceTextView.setText("");
        } else {
            originalPriceTextView.setText(originalPrice + " Ft");
        } */

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}