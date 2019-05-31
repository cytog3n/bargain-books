package com.cyto.bargainbooks.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import com.cyto.bargainbooks.R;
import com.cyto.bargainbooks.adapter.ConfigListAdapter;
import com.cyto.bargainbooks.config.Config;
import com.cyto.bargainbooks.config.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigFragment extends Fragment {

    private ConfigFragment.OnFragmentInteractionListener mListener;

    private View view;

    private Config config;

    private final List<Pair> listTitle = new ArrayList<>();

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_config, container, false);
        config = Config.getInstance(getContext());

        ListView listView = view.findViewById(R.id.listView);
        saleLevelSwitch = view.findViewById(R.id.saleLevelValue);
        Switch showLibri5PercentDealsSwitch = view.findViewById(R.id.libri5percentValue);

        Context context = getActivity().getApplicationContext();

        for (String s : config.getStoreFilter().keySet().stream().sorted(String::compareToIgnoreCase).collect(Collectors.toCollection(ArrayList::new))) {
            listTitle.add(new Pair(s, config.getStoreFilter().get(s)));
        }

        ConfigListAdapter listAdapter = new ConfigListAdapter(context, listTitle);
        listView.setAdapter(listAdapter);

        if (config.getSaleLevel().equals(Constant.bookLevel)) {
            saleLevelSwitch.setText(R.string.book_level);
            saleLevelSwitch.setChecked(false);
        } else {
            saleLevelSwitch.setText(R.string.store_level);
            saleLevelSwitch.setChecked(true);
        }

        saleLevelSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    saleLevelSwitch.setText(R.string.store_level);
                    config.setSaleLevel(Constant.storeLevel);
                } else {
                    saleLevelSwitch.setText(R.string.book_level);
                    config.setSaleLevel(Constant.bookLevel);
                }
            }
        });

        showLibri5PercentDealsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                config.setShowLibri5PercentDeals(isChecked);
            }
        });
        showLibri5PercentDealsSwitch.setChecked(config.getShowLibri5PercentDeals());

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

}
