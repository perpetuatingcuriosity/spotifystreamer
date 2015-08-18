package com.example.pcurio.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.pcurio.spotifystreamer.model.Track;

import java.util.ArrayList;


public class TrackActivity extends AppCompatActivity implements TrackFragment.PlaybackListener {
    public static final String TAG = TrackActivity.class.getSimpleName();

    public static final String TRACK_FRAGMENT_TAG = "TFTAG";

    private static final String ARTIST_NAME_KEY = "artist_name";

    private Context mContext;

    private TrackFragment mTrackFragment;

    private String mArtistName;
    private String mSpotifyID;

    //------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        mContext = this;

        if(savedInstanceState == null){

            mTrackFragment = new TrackFragment();

            //Retrieve intent data
            Intent intent = getIntent();
            mArtistName = intent.getStringExtra(Utils.ARTIST_NAME);
            mSpotifyID = intent.getStringExtra(Utils.SPOTIFY_ID);

            //Set arguments for TrackFragment
            Bundle b = new Bundle();
            b.putString(Utils.SPOTIFY_ID, mSpotifyID);
            mTrackFragment.setArguments(b);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.track_fragment_container, mTrackFragment, TRACK_FRAGMENT_TAG)
                    .commit();

        } else {

            mTrackFragment = (TrackFragment) getSupportFragmentManager().findFragmentByTag(TRACK_FRAGMENT_TAG);

            if(savedInstanceState.containsKey(ARTIST_NAME_KEY)){
                mArtistName = savedInstanceState.getString(ARTIST_NAME_KEY);
            }

        }

        //Set artist name as actionbar subtitle
        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null){
            actionBar.setSubtitle(mArtistName);
        }

    } //onCreate

    @Override
    public void playTrack(ArrayList<Track> trackList, int selectedTrack) {

        Intent trackIntent = new Intent(TrackActivity.this, PlaybackActivity.class);
        trackIntent.putParcelableArrayListExtra(Utils.TRACK_LIST, trackList);
        trackIntent.putExtra(Utils.SELECTED_TRACK_POSITION, selectedTrack);
        trackIntent.putExtra(Utils.ARTIST_NAME, mArtistName);
        trackIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(trackIntent);

    }


    //SaveInstanceState
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
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
