package com.cyto.bargainbooks.adapter;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cyto.bargainbooks.R;
import com.cyto.bargainbooks.config.Constants;
import com.cyto.bargainbooks.model.Book;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ExpandableBookListAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final List<Pair> expandableListTitle;
    private final Map<String, List<Book>> expandableListDetail;

    public ExpandableBookListAdapter(Context context, List<Pair> expandableListTitle, Map<String, List<Book>> expandableListDetail) {
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
            convertView = layoutInflater.inflate(R.layout.book_level_list_item, null);
        }


        TextView shopName = convertView.findViewById(R.id.shopName);
        if (book.getStore() == null) {
            shopName.setText(context.getString(R.string.no_available_store));
        } else {
            shopName.setText(Constants.storeMap.get(book.getStore()));
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

        List<Book> books = this.expandableListDetail.get(book.getISBN());

        LinearLayout row = convertView.findViewById(R.id.row);
        if (books != null) {
            Book min = books.stream().max(Comparator.comparing(Book::getSalePercent)).get();

            if (min.getSalePercent().equals(book.getSalePercent())) {
                row.setBackgroundColor(context.getColor(R.color.holo_blue_light));
            } else {
                row.setBackgroundColor(0); // set default background
            }
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
            convertView = layoutInflater.inflate(R.layout.book_level_list_group, null);
        }

        Book b = (Book) (((Pair) getGroup(listPosition)).second);

        if (isExpanded && b.getOriginalPrice() != null) {
            View v = convertView.findViewById(R.id.original_price_parent);
            if (v != null) {
                v.setVisibility(View.VISIBLE);
            }
        } else {
            View v = convertView.findViewById(R.id.original_price_parent);
            if (v != null) {
                v.setVisibility(View.GONE);
            }
        }

        TextView titleTextView = convertView.findViewById(R.id.title);
        titleTextView.setText(b.getTitle());

        TextView authorTextView = convertView.findViewById(R.id.author);
        authorTextView.setText(b.getAuthor());

        TextView saleTextView = convertView.findViewById(R.id.off);
        if (b.getSalePercent() == null) {
            saleTextView.setText("");
        } else {
            saleTextView.setText(b.getSalePercent() + "%");
        }

        TextView originalPriceTextView = convertView.findViewById(R.id.original_price_value);
        if (b.getOriginalPrice() == null) {
            originalPriceTextView.setText("");
        } else {
            originalPriceTextView.setText(b.getOriginalPrice() + " Ft");
        }

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