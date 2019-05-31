package com.cyto.bargainbooks.adapter;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.cyto.bargainbooks.R;
import com.cyto.bargainbooks.config.Config;
import com.cyto.bargainbooks.config.Constant;

import java.util.List;

public class ConfigListAdapter extends BaseAdapter {

    private final Context context;
    private final List<Pair> listTitle;
    private Config config;

    public ConfigListAdapter(Context context, List<Pair> listTitle) {
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
            convertView = layoutInflater.inflate(R.layout.config_filter_list_item, null);
        }

        config = Config.getInstance(context);

        Pair p = (Pair) getItem(position);

        TextView storeName = convertView.findViewById(R.id.store_name);
        Switch storeValue = convertView.findViewById(R.id.store_value);

        storeName.setText(Constant.storeMap.get(p.first));
        storeValue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                config.getStoreFilter().replace((String) p.first, isChecked);
            }
        });

        storeValue.setChecked(config.getStoreFilter().get(p.first));
        return convertView;
    }
}
