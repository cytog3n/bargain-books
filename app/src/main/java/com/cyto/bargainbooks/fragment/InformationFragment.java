package com.cyto.bargainbooks.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.cyto.bargainbooks.R;

public class InformationFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private ScrollView scrollView;

    private View myLibraryTitleView;

    public InformationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment InformationFragment.
     */
    public static InformationFragment newInstance() {
        InformationFragment fragment = new InformationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("InformationFragment", "Created");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_information, container, false);

        scrollView = view.findViewById(R.id.fragment_info_scroll);

        String elemToScroll = null;
        try {
            elemToScroll = this.getArguments().getString("element");
        } catch (NullPointerException e) {
            Log.d("InformationFragment", "No 'element' in the bundle.");
        }

        if (elemToScroll != null) {
            View elem = view.findViewById(getResources().getIdentifier((String) elemToScroll, "id", this.getContext().getPackageName()));
            if (elem != null) {
                elem.requestFocus();
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.smoothScrollBy(0, elem.getTop());
                    }
                });
            }
        }

        myLibraryTitleView = view.findViewById(R.id.mylibrary_info_title);

        myLibraryTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.vgm.mylibrary")));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.vgm.mylibrary")));
                }
            }
        });

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
        Log.i("InformationFragment", "Attached");
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i("InformationFragment", "Detached");
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href="http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
