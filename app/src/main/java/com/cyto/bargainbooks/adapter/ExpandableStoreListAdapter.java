package com.cyto.bargainbooks.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.navigation.Navigation;

import com.cyto.bargainbooks.R;
import com.cyto.bargainbooks.config.Constants;
import com.cyto.bargainbooks.model.Book;

import java.util.List;
import java.util.Map;

public class ExpandableStoreListAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final List<Pair> expandableListTitle;
    private final Map<String, List<Book>> expandableListDetail;
    private final Activity activity;

    public ExpandableStoreListAdapter(Context context, Activity activity, List<Pair> expandableListTitle, Map<String, List<Book>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
        this.activity = activity;
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
    public View getChildView(int listPosition, final int expandedListPosition, boolean isLastChild, View convertView, ViewGroup parent) {

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
            off.setText(context.getString(R.string.empty_string));
        } else {
            off.setText(String.format(context.getString(R.string.sale_percent_parenthesis), book.getSalePercent()));
        }

        TextView price = convertView.findViewById(R.id.price);
        if (book.getNewPrice() == null) {
            price.setText(context.getString(R.string.empty_string));
        } else {
            price.setText(String.format(context.getString(R.string.price_tag_huf), book.getNewPrice()));
        }

        Button compareBtn = convertView.findViewById(R.id.compareBtn);
        compareBtn.setFocusable(false);

        compareBtn.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("isbn", book.getISBN());
            Navigation.findNavController(activity.findViewById(R.id.nav_host_fragment)).navigate(R.id.BookDetailFragment, bundle);
        });

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
    public View getGroupView(int listPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.store_level_list_group, null);
        }

        Integer count = (Integer) ((Pair) getGroup(listPosition)).second;
        String shopName = (String) ((Pair) getGroup(listPosition)).first;
        shopName = Constants.storeMap.get(shopName);

        TextView shopNameTextView = convertView.findViewById(R.id.store_name);
        shopNameTextView.setText(shopName);

        TextView countTextView = convertView.findViewById(R.id.sale_count);
        countTextView.setText(count.toString());

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