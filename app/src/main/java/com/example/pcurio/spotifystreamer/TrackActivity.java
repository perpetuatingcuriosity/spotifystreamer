package com.example.pcurio.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class TrackActivity extends AppCompatActivity implements Utils.trackSelectionListener {
    public static final String TAG = TrackActivity.class.getSimpleName();

    private Context mContext;

    private SpotifyApi api;
    private SpotifyService spotify;

    private ArrayList<ArtistTrackItem> mTopTrackList = new ArrayList<>();
    private RecyclerView mTrackRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mTrackAdapter;

    private static final String TRACK_LIST_KEY = "track_list";
    private static final String ARTIST_NAME_KEY = "artist_name";

    private TextView mEmptyTextView;
    private String mArtistName;
    private String mSpotifyID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        mContext = this;

        //Spotify
        api = new SpotifyApi();
        spotify = api.getService();

        //RecyclerView
        mTrackRecyclerView = (RecyclerView) findViewById(R.id.trackRecyclerView);
        mLayoutManager = new LinearLayoutManager(mContext);
        mTrackRecyclerView.setLayoutManager(mLayoutManager);
        mTrackAdapter = new TrackListAdapter(this, mTopTrackList, this);
        mTrackRecyclerView.setAdapter(mTrackAdapter);
        mTrackRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mTrackRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        mEmptyTextView = (TextView) findViewById(R.id.empty_track_no_tracks);

        if(savedInstanceState != null) {

            mTopTrackList = savedInstanceState.getParcelableArrayList(TRACK_LIST_KEY);

            mTrackAdapter = new TrackListAdapter(this, mTopTrackList, this);
            mTrackRecyclerView.setAdapter(mTrackAdapter);

            mArtistName = savedInstanceState.getString(ARTIST_NAME_KEY);

        } else {
            //Retrieve intent data
            Intent intent = getIntent();
            mArtistName = intent.getStringExtra(Utils.ARTIST_NAME);
            mSpotifyID = intent.getStringExtra(Utils.SPOTIFY_ID);

            getTopTracks();
        }

        //Set artist name as actionbar subtitle
        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null){
            actionBar.setSubtitle(mArtistName);
        }

    } //onCreate

    private void getTopTracks(){

        Map<String, Object> options = new HashMap<>();
        options.put("country", Utils.COUNTRY_CODE);

        spotify.getArtistTopTrack(mSpotifyID, options, new Callback<Tracks>() {
            @Override
            public void success(Tracks tracks, Response response) {
                Log.d(TAG, "getArtistTopTrack > success");
                mTopTrackList.clear();

                mEmptyTextView.setVisibility(View.GONE);

                List<Track> trackList = tracks.tracks;

                if (trackList.size() == 0) {
                    mTrackAdapter.notifyDataSetChanged();
                    mEmptyTextView.setVisibility(View.VISIBLE);
                    return;
                }

                while (mTopTrackList.size() < 10) {

                    for (Track track : trackList) {

                        ArtistTrackItem singleTrackItem = new ArtistTrackItem();
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

                mTrackAdapter.notifyDataSetChanged();

            } //success

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "getArtistTopTrack > failure: " + error.getMessage());
            } //failure
        });
    }

    @Override
    public void onTrackClicked(String previewUrl) {
        //TODO: Spotify Streamer, pt. 2
        Toast.makeText(this, "Start playing track preview", Toast.LENGTH_SHORT).show();
    }

    //SaveInstanceState
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putParcelableArrayList(TRACK_LIST_KEY, mTopTrackList);
        savedInstanceState.putString(ARTIST_NAME_KEY, mArtistName);

    }

    //-----------------------------------------------------------------------------
    //MENU
    //-----------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tracks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
} //TrackActivity
