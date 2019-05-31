package com.cyto.bargainbooks.adapter;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cyto.bargainbooks.R;
import com.cyto.bargainbooks.model.Book;

import java.text.SimpleDateFormat;
import java.util.List;

public class WishlistListAdapter extends BaseAdapter {

    private final Context context;
    private final List<Pair> listTitle;
    private final SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");

    public WishlistListAdapter(Context context, List<Pair> listTitle) {
        this.context = context;
        this.listTitle = listTitle;
    }

    @Override
    public int getCount() {
        return listTitle.size();
    }

    @Override
    public Object getItem(int position) {
        return listTitle.get(position);
    }

    @Override
    public long getItemId(int position) {
        return listTitle.get(position).first.hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.wishlist_list_item, null);
        }

        Book b = (Book) (((Pair) getItem(position)).second);

        TextView titleTextView = convertView.findViewById(R.id.title);
        titleTextView.setText(b.getTitle());

        TextView authorTextView = convertView.findViewById(R.id.author);
        authorTextView.setText(b.getAuthor());

        TextView updateDate = convertView.findViewById(R.id.last_updated_value);
        if (b.getLastUpdateDate() != null) {
            updateDate.setText(format.format(b.getLastUpdateDate()));
        } else {
            updateDate.setText("");
        }


        return convertView;
    }
}
