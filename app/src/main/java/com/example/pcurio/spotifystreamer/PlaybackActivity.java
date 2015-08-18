package com.example.pcurio.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.pcurio.spotifystreamer.model.Track;

import java.util.ArrayList;


public class PlaybackActivity extends AppCompatActivity {
    private static final String TAG = PlaybackActivity.class.getSimpleName();

    private static final String PLAYBACK_FRAGMENT_TAG = "PTAG";

    private Context mContext;

    private PlaybackFragment mPlaybackFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback);

        mContext = this;

        mPlaybackFragment = new PlaybackFragment();

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //Retrieve intent data
        Intent intent = getIntent();

        if(intent != null){
            ArrayList<Track> trackList = intent.getParcelableArrayListExtra(Utils.TRACK_LIST);
            int selectedTrack = intent.getIntExtra(Utils.SELECTED_TRACK_POSITION, 0);
            String artistName = intent.getStringExtra(Utils.ARTIST_NAME);

            //Set arguments for PlaybackFragment
            Bundle b = new Bundle();
            b.putParcelableArrayList(Utils.TRACK_LIST, trackList);
            b.putInt(Utils.SELECTED_TRACK_POSITION, selectedTrack);
            b.putString(Utils.ARTIST_NAME, artistName);
            mPlaybackFragment.setArguments(b);

        } else {
            Log.d(TAG, "intent is null");
        }

        getSupportFragmentManager().beginTransaction()
                .add(R.id.playback_fragment_container, mPlaybackFragment, PLAYBACK_FRAGMENT_TAG)
                .commit();
    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_playback, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
