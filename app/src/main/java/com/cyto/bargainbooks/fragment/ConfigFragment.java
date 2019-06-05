package com.cyto.bargainbooks.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.cyto.bargainbooks.R;
import com.cyto.bargainbooks.config.Constants;
import com.cyto.bargainbooks.storage.Config;

import org.apache.commons.collections4.map.HashedMap;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfigFragment extends Fragment {

    private ConfigFragment.OnFragmentInteractionListener mListener;

    private View view;

    private Config config;

    private Map<String, Object> optionsMap = new HashedMap<>();

    private Switch saleLevelSwitch;

    public ConfigFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ConfigFragment.
     */
    public static ConfigFragment newInstance() {
        ConfigFragment fragment = new ConfigFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.config_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {

            config.setSaleLevel((String) optionsMap.get("saleLevel"));
            config.setShowLibri5PercentDeals((Boolean) optionsMap.get("show5percent"));

            for (String store : config.getStoreFilter().keySet()) {
                config.getStoreFilter().replace(store, (Boolean) optionsMap.get(store));
            }

            Snackbar.make(view, "Saved!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            Config.saveConfigToSharedPreferences(getContext());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.i("ConfigFragment", "Created");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_config, container, false);
        ((NavigationView) getActivity().findViewById(R.id.nav_view)).setCheckedItem(R.id.nav_config);
        config = Config.getInstance(getContext());
        optionsMap.clear();
        saleLevelSwitch = view.findViewById(R.id.saleLevelValue);
        Switch showLibri5PercentDealsSwitch = view.findViewById(R.id.libri5percentValue);
        LinearLayout stores = view.findViewById(R.id.stores);

        optionsMap.put("saleLevel", config.getSaleLevel());
        optionsMap.put("show5percent", config.getShowLibri5PercentDeals());
        if (optionsMap.get("saleLevel").equals(Constants.bookLevel)) {
            saleLevelSwitch.setText(R.string.book_level);
            saleLevelSwitch.setChecked(false);
        } else {
            saleLevelSwitch.setText(R.string.store_level);
            saleLevelSwitch.setChecked(true);
        }

        saleLevelSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                saleLevelSwitch.setText(R.string.store_level);
                optionsMap.replace("saleLevel", Constants.storeLevel);
            } else {
                saleLevelSwitch.setText(R.string.book_level);
                optionsMap.replace("saleLevel", Constants.bookLevel);
            }
        });
        showLibri5PercentDealsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> optionsMap.replace("show5percent", isChecked));
        showLibri5PercentDealsSwitch.setChecked((Boolean) optionsMap.get("show5percent"));

        populateFilters(inflater, stores);

        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i("ConfigFragment", "Attached");
        if (context instanceof ConfigFragment.OnFragmentInteractionListener) {
            mListener = (ConfigFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i("ConfigFragment", "Detached");
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Populate the filters into the given LinearLayout
     */
    private void populateFilters(LayoutInflater inflater, LinearLayout stores) {

        for (String store : config.getStoreFilter().keySet().stream().sorted(String::compareToIgnoreCase).collect(Collectors.toCollection(ArrayList::new))) {
            View view = inflater.inflate(R.layout.config_filter_list_item, stores, false);
            optionsMap.put(store, config.getStoreFilter().get(store));

            ((TextView) view.findViewById(R.id.store_name)).setText(Constants.storeMap.get(store));
            Switch storeValue = view.findViewById(R.id.store_value);
            storeValue.setOnCheckedChangeListener((buttonView, isChecked) -> optionsMap.replace(store, isChecked));
            storeValue.setChecked((Boolean) optionsMap.get(store));
            stores.addView(view);
        }
    }

}
