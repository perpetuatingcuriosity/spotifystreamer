package com.example.pcurio.spotifystreamer;


import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pcurio.spotifystreamer.model.Artist;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SearchFragment extends android.support.v4.app.Fragment {
    public static final String TAG = SearchFragment.class.getSimpleName();

    private static final String SEARCH_LIST_KEY = "search_list";

    private Activity mActivity;

    private SpotifyApi api;
    private SpotifyService spotify;

    private ArrayList<Artist> mSearchList = new ArrayList<>();

    private RecyclerView mSearchRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mSearchAdapter;

    private Utils.artistSelectionListener mArtistSelectionListener;

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

        return rootView;

    } //onCreateView

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Spotify
        api = new SpotifyApi();
        spotify = api.getService();

        mEmptyTextView = (TextView) mActivity.findViewById(R.id.empty_main_no_artists);

        //RecyclerView
        mSearchRecyclerView = (android.support.v7.widget.RecyclerView) mActivity.findViewById(R.id.searchRecyclerView);
        mLayoutManager = new LinearLayoutManager(mActivity);
        mSearchRecyclerView.setLayoutManager(mLayoutManager);
        mSearchRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mSearchRecyclerView.addItemDecoration(new DividerItemDecoration(mActivity, LinearLayoutManager.VERTICAL));

        mArtistSelectionListener = new Utils.artistSelectionListener() {
            @Override
            public void onArtistClicked(String spotifyID, String artistName) {
                mCallback.displayTracks(spotifyID, artistName);
            }
        };

        if(savedInstanceState != null && savedInstanceState.containsKey(SEARCH_LIST_KEY)) {
            mSearchList = savedInstanceState.getParcelableArrayList(SEARCH_LIST_KEY);
        }

        mSearchAdapter = new SearchAdapter(mActivity, mSearchList, mArtistSelectionListener);
        mSearchRecyclerView.setAdapter(mSearchAdapter);

        final SearchView searchView = (SearchView) mActivity.findViewById(R.id.search_bar);

        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint(mActivity.getString(R.string.search_artist_query_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                showArtists(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    } //onActivityCreated

    public void showArtists(String query){

        //Execute search for artist
        spotify.searchArtists(query, new Callback<ArtistsPager>() {
            @Override
            public void success(ArtistsPager artistsPager, Response response) {
                Log.d(TAG, "searchArtists > success");
                mSearchList.clear();

                List<kaaes.spotify.webapi.android.models.Artist> artists = artistsPager.artists.items;

                for (kaaes.spotify.webapi.android.models.Artist artist : artists) {

                    Artist singleListItem = new Artist();
                    singleListItem.setSpotifyID(artist.id);
                    singleListItem.setArtistName(artist.name);

                    List<Image> albumArt = artist.images;

                    if (albumArt.size() > 0) {
                        singleListItem.setArtistThumbnail(albumArt.get(0).url);
                    }

                    mSearchList.add(singleListItem);
                }

                mEmptyTextView.setVisibility(View.GONE);

                if (mSearchList == null || mSearchList.size() == 0) {
                    mEmptyTextView.setVisibility(View.VISIBLE);
                    mSearchAdapter.notifyDataSetChanged();
                }

                mSearchAdapter.notifyDataSetChanged();

            } //success

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "searchArtists > failure: ");

                Toast.makeText(mActivity, "Oops! There was a problem searching. " +
                        "Try your search again.", Toast.LENGTH_SHORT).show();

            } //failure
        });

    } //showArtists


    //SaveInstanceState
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList(SEARCH_LIST_KEY, mSearchList);
        super.onSaveInstanceState(savedInstanceState);

    }

} //SearchFragment
