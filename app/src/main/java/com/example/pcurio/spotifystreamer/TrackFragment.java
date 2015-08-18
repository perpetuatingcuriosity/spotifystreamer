package com.example.pcurio.spotifystreamer;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pcurio.spotifystreamer.model.Track;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class TrackFragment extends android.support.v4.app.Fragment {
    public static final String TAG = TrackFragment.class.getSimpleName();

    private Activity mActivity;

    private SpotifyApi api;
    private SpotifyService spotify;

    private ArrayList<Track> mTopTrackList = new ArrayList<>();

    private RecyclerView mTrackRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mTrackAdapter;

    private Utils.trackSelectionListener mTrackSelectionListener;

    private static final String TRACK_LIST_KEY = "track_list";

    private TextView mEmptyTextView;

    //Interface with Activity
    public PlaybackListener mCallback;

    //------------------------------------------------------------------

    public interface PlaybackListener {
        void playTrack(ArrayList<Track> trackList, int selectedTrack);
    }

    public TrackFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mActivity = activity;

        try {
            mCallback = (PlaybackListener) activity;
        } catch(ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + "must implement TrackFragment.PlaybackListener");
        }
    } //onAttach

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_track, container, false);

        return rootView;
    } //onCreateView

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mActivity = getActivity();

        //Spotify
        api = new SpotifyApi();
        spotify = api.getService();

        mEmptyTextView = (TextView) mActivity.findViewById(R.id.empty_track_no_tracks);

        //RecyclerView
        mTrackRecyclerView = (android.support.v7.widget.RecyclerView) mActivity.findViewById(R.id.trackRecyclerView);
        mLayoutManager = new LinearLayoutManager(mActivity);
        mTrackRecyclerView.setLayoutManager(mLayoutManager);
        mTrackRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mTrackRecyclerView.addItemDecoration(new DividerItemDecoration(mActivity, LinearLayoutManager.VERTICAL));

        mTrackSelectionListener = new Utils.trackSelectionListener() {
            @Override
            public void onTrackClicked(ArrayList<Track> trackList, int trackPosition) {
                mCallback.playTrack(trackList, trackPosition);
            }
        };

        if(savedInstanceState != null && savedInstanceState.containsKey(TRACK_LIST_KEY)) {
            mTopTrackList = savedInstanceState.getParcelableArrayList(TRACK_LIST_KEY);

        } else {

            Bundle b = getArguments();

            if(b != null){
                String spotifyID = b.getString(Utils.SPOTIFY_ID);
                getTopTracks(spotifyID);
            }
        }

        mTrackAdapter = new TrackListAdapter(mActivity, mTopTrackList, mTrackSelectionListener);
        mTrackRecyclerView.setAdapter(mTrackAdapter);

    } //onActivityCreated

    private void getTopTracks(String spotifyID){

        Map<String, Object> options = new HashMap<>();
        options.put("country", Utils.COUNTRY_CODE);

        spotify.getArtistTopTrack(spotifyID, options, new Callback<Tracks>() {
            @Override
            public void success(Tracks tracks, Response response) {
                Log.d(TAG, "getArtistTopTrack > success");

                mTopTrackList.clear();

                List<kaaes.spotify.webapi.android.models.Track> trackList = tracks.tracks;

                while (mTopTrackList.size() < 10) {

                    for (kaaes.spotify.webapi.android.models.Track track : trackList) {

                        Track singleTrackItem = new Track();
                        singleTrackItem.setTrackName(track.name);
                        singleTrackItem.setAlbumName(track.album.name);
                        singleTrackItem.setPreviewUrl(track.preview_url);

                        List<Image> albumArt = track.album.images;

                        if (albumArt.size() > 0) {

                            String largeUrl = albumArt.get(0).url;
                            String smallUrl = albumArt.get(1).url;

                            if (largeUrl != null) {
                                singleTrackItem.setAlbumThumbnailLarge(largeUrl);
                            }

                            if (smallUrl != null) {
                                singleTrackItem.setAlbumThumbnailSmall(smallUrl);
                            }
                        }

                        mTopTrackList.add(singleTrackItem);
                    }
                }

                mEmptyTextView.setVisibility(View.GONE);

                if (mTopTrackList == null || mTopTrackList.size() == 0) {
                    mTrackAdapter.notifyDataSetChanged();
                    mEmptyTextView.setVisibility(View.VISIBLE);
                }

                mTrackAdapter.notifyDataSetChanged();

            } //success

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "getArtistTopTrack > failure: " + error.getMessage());

                Toast.makeText(mActivity, getString(R.string.toast_top_track_request_fail),
                        Toast.LENGTH_SHORT).show();

            } //failure
        });
    } //getTopTracks

    //SaveInstanceState
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList(TRACK_LIST_KEY, mTopTrackList);
    }

} //TrackFragment
