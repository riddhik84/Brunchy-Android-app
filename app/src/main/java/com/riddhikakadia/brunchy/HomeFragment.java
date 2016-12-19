package com.riddhikakadia.brunchy;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;

public class HomeFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    ListView homeListView;
    HomeListAdapter homeListAdapter;

    String[] recipeList = new String[]{
            "Healthy Breakfast",
            "Cookies",
            "Soup",
            "Sandwich",
            "Juices",
            "Barbecue",
            "Rice",
            "Salad",
            "Vegetarian",
            "Pasta",
            "Pizza",
            "Stew",
            "Cake",
            "Smoothie"
    };

    Integer[] recipeImages = new Integer[]{
            R.drawable.breakfast,
            R.drawable.cookies,
            R.drawable.soup,
            R.drawable.sandwich,
            R.drawable.juice,
            R.drawable.barbecue,
            R.drawable.rice,
            R.drawable.salad,
            R.drawable.vegetarian,
            R.drawable.pasta,
            R.drawable.pizza,
            R.drawable.stew,
            R.drawable.cake,
            R.drawable.smoothie
    };

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        homeListView = (ListView) rootView.findViewById(R.id.home_list_view);
        homeListView.setDivider(null);
        homeListView.setDividerHeight(0);
        homeListAdapter = new HomeListAdapter(getActivity(), recipeList, recipeImages);
        homeListView.setAdapter(homeListAdapter);


        return rootView;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
