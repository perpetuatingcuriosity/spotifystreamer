package com.example.pcurio.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.pcurio.spotifystreamer.model.Track;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchFragment.SearchListener,
        TrackFragment.PlaybackListener{
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String TRACK_FRAGMENT_TAG = "TFTAG";

    private Context mContext;

    private TrackFragment mTrackFragment;

    private boolean mTwoPane;

    private String mArtistName;

    //------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        if(findViewById(R.id.track_fragment_container)!= null) {
            mTwoPane = true;

            mTrackFragment = new TrackFragment();

            if(savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.track_fragment_container, mTrackFragment,
                                TRACK_FRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }

    } //onCreate

    //-----------------------------------------------------------------------------
    // CALLBACKS
    //-----------------------------------------------------------------------------

    @Override
    public void onArtistsSearched() {
        if(mTwoPane){
            mTrackFragment.clearTracks();
        }
    }

    @Override
    public void displayTracks(String spotifyID, String artistName) {

        if(mTwoPane){

            Bundle b = new Bundle();
            b.putString(Utils.SPOTIFY_ID, spotifyID);
            b.putString(Utils.ARTIST_NAME, artistName);

            mArtistName = artistName;

            mTrackFragment = new TrackFragment();
            mTrackFragment.setArguments(b);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.track_fragment_container, mTrackFragment,
                            TRACK_FRAGMENT_TAG)
                    .commit();

        } else {

            Intent trackIntent = new Intent(MainActivity.this, TrackActivity.class);
            trackIntent.putExtra(Utils.ARTIST_NAME, artistName);
            trackIntent.putExtra(Utils.SPOTIFY_ID, spotifyID);
            trackIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(trackIntent);
        }

    } //displayTracks

    @Override
    public void playTrack(ArrayList<Track> trackList, int selectedTrack) {

        if(mTwoPane){

            // Create the fragment and show it as a dialog
            PlaybackFragment playbackFragment = new PlaybackFragment();

            Bundle b = new Bundle();
            b.putString(Utils.ARTIST_NAME, mArtistName);
            b.putParcelableArrayList(Utils.TRACK_LIST, trackList);
            b.putInt(Utils.SELECTED_TRACK_POSITION, selectedTrack);

            playbackFragment.setArguments(b);

            playbackFragment.show(getSupportFragmentManager(), "PLAYBACK_FRAG");

        } else {

            Intent playbackIntent = new Intent(MainActivity.this, PlaybackActivity.class);
            playbackIntent.putParcelableArrayListExtra(Utils.TRACK_LIST, trackList);
            playbackIntent.putExtra(Utils.SELECTED_TRACK_POSITION, selectedTrack);
            playbackIntent.putExtra(Utils.ARTIST_NAME, mArtistName);
            startActivity(playbackIntent);
        }

    }
} //MainActivity
