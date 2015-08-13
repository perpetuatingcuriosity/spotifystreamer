package com.example.pcurio.spotifystreamer;


import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchFragment extends android.support.v4.app.Fragment implements Utils.artistSelectionListener {
    public static final String TAG = SearchFragment.class.getSimpleName();

    private static final String SEARCH_LIST_KEY = "search_list";

    private Activity mActivity;

    private ArrayList<ArtistListItem> mSearchList = new ArrayList<>();

    private RecyclerView mSearchRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mSearchAdapter;

    private TextView mEmptyTextView;

    //Interface with Activity
    public TrackListener mCallback;

    //------------------------------------------------------------------

    public interface TrackListener {
        void displayTracks(String spotifyID, String artistName);
    }

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mActivity = activity;

        try {
            mCallback = (TrackListener) activity;
        } catch(ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + "must implement SearchFragment.TrackListener");
        }
    } //onAttach

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        //RecyclerView
        mSearchRecyclerView = (android.support.v7.widget.RecyclerView) rootView.findViewById(R.id.searchRecyclerView);
        mLayoutManager = new LinearLayoutManager(mActivity);
        mSearchAdapter = new SearchListAdapter(mActivity, mSearchList, this);
        mSearchRecyclerView.setAdapter(mSearchAdapter);
        mSearchRecyclerView.setLayoutManager(mLayoutManager);
        mSearchRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mSearchRecyclerView.addItemDecoration(new DividerItemDecoration(mActivity, LinearLayoutManager.VERTICAL));

        mSearchAdapter = new SearchListAdapter(mActivity, mSearchList, this);
        mSearchRecyclerView.setAdapter(mSearchAdapter);

        mEmptyTextView = (TextView) rootView.findViewById(R.id.empty_main_no_artists);

        if(savedInstanceState != null && savedInstanceState.containsKey(SEARCH_LIST_KEY)) {
            mSearchList = savedInstanceState.getParcelableArrayList(SEARCH_LIST_KEY);

            mSearchAdapter = new SearchListAdapter(mActivity, mSearchList, this);
            mSearchRecyclerView.setAdapter(mSearchAdapter);
        }

        return rootView;

    } //onCreateView

    public void updateArtistResults(ArrayList<ArtistListItem> searchResults){
        mSearchList = searchResults;

        mEmptyTextView.setVisibility(View.GONE);

        if(mSearchList == null || mSearchList.size() == 0) {
            mSearchAdapter.notifyDataSetChanged();
            mEmptyTextView.setVisibility(View.VISIBLE);
        }

        mSearchAdapter = new SearchListAdapter(mActivity, mSearchList, this);
        mSearchRecyclerView.setAdapter(mSearchAdapter);

    } //updateArtistResults

    //SaveInstanceState
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList(SEARCH_LIST_KEY, mSearchList);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onArtistClicked(String spotifyID, String artistName) {
        mCallback.displayTracks(spotifyID, artistName);
    }

} //SearchFragment
