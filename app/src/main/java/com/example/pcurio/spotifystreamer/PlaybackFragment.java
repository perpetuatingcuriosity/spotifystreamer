package com.example.pcurio.spotifystreamer;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pcurio.spotifystreamer.model.Track;

import java.util.ArrayList;

public class PlaybackFragment extends android.support.v4.app.DialogFragment {
    public static final String TAG = PlaybackFragment.class.getSimpleName();

    private Activity mActivity;

    private MusicService musicService;
    private Intent playIntent;
    private boolean musicBound = false;

    private boolean isPlaying;

    private ArrayList<Track> mTrackList;
    private int selectedTrackPosition;

    //Track Info
    private TextView mArtistName;
    private TextView mTrackTitle;
    private TextView mAlbumTitle;
    private ImageView mAlbumArt;

    //Player Controls
    private ImageButton mPreviousButton;
    private ImageButton mPlayPauseButton;
    private ImageButton mNextButton;

    private TextView mTrackStartTime;
    private TextView mTrackEndTime;

    //Seekbar
    private SeekBar mSeekBar;
    private Intent seekbarIntent;
    private int seekMax;
    private static int songEnded = 0;
    boolean mBroadcastIsRegistered;
    private BroadcastReceiver broadcastReceiver;

    public static final String BROADCAST_SEEKBAR = "com.example.pcurio.spotifystreamer.sendseekbar";


    //------------------------------------------------------------------

    public PlaybackFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_playback, container, false);

        setRetainInstance(true);

        mArtistName = (TextView) rootView.findViewById(R.id.artist_name);
        mTrackTitle = (TextView) rootView.findViewById(R.id.track_title);
        mAlbumTitle = (TextView) rootView.findViewById(R.id.album_title);

        mAlbumArt = (ImageView) rootView.findViewById(R.id.album_artwork);

        mPreviousButton = (ImageButton) rootView.findViewById(R.id.playback_button_previous);
        mPlayPauseButton = (ImageButton) rootView.findViewById(R.id.playback_button_play_pause);
        mNextButton = (ImageButton) rootView.findViewById(R.id.playback_button_next);

        mTrackStartTime = (TextView) rootView.findViewById(R.id.seekbar_start_time);
        mTrackEndTime = (TextView) rootView.findViewById(R.id.seekbar_end_time);

        mSeekBar = (SeekBar) rootView.findViewById(R.id.seekbar);

        //Get track info from intent
        Bundle b = getArguments();

        if(b != null){
            mTrackList = b.getParcelableArrayList(Utils.TRACK_LIST);
            selectedTrackPosition = b.getInt(Utils.SELECTED_TRACK_POSITION);
            String artistName = b.getString(Utils.ARTIST_NAME);

            mArtistName.setText(artistName);
            updateUI(selectedTrackPosition);
        }

        //Media Controls
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(selectedTrackPosition > 0){
                    selectedTrackPosition--;
                }

                updateUI(selectedTrackPosition);

                musicService.setTrack(selectedTrackPosition);
                musicService.playTrack();
            }
        });

        mPlayPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (musicService.isPlaying()) {
                    musicService.pauseTrack();

                    mPlayPauseButton.setImageDrawable(getResources()
                            .getDrawable(android.R.drawable.ic_media_play));

                    isPlaying = false;

                } else {
                    musicService.resumeTrack();

                    mPlayPauseButton.setImageDrawable(getResources()
                            .getDrawable(android.R.drawable.ic_media_pause));

                    isPlaying = false;
                }

            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(selectedTrackPosition == mTrackList.size()-1){
                    selectedTrackPosition = 0;
                } else {
                    selectedTrackPosition ++;
                }

                updateUI(selectedTrackPosition);

                musicService.setTrack(selectedTrackPosition);
                musicService.playTrack();

            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if(musicService != null && b){
                    int seekPosition = seekBar.getProgress();
                    seekbarIntent.putExtra("seekpos", seekPosition);
                    mActivity.sendBroadcast(seekbarIntent);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mActivity = getActivity();

        //TODO: check connectivity

        if(playIntent == null){
            playIntent = new Intent(mActivity, MusicService.class);
            mActivity.getApplicationContext().bindService(playIntent, playerConnection, Context.BIND_AUTO_CREATE);
            mActivity.startService(playIntent);
            Log.v(TAG, "Service started");
        }

        seekbarIntent = new Intent(BROADCAST_SEEKBAR);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateTrackData(intent);
            }
        };


    } //onActivityCreated

    @Override
    public void onResume() {
        super.onResume();

        if(!mBroadcastIsRegistered) {
            mActivity.registerReceiver(broadcastReceiver, new IntentFilter(
                    MusicService.BROADCAST_ACTION));

            mBroadcastIsRegistered = true;
        }
    }

    //Connect to Music Player Service
    private ServiceConnection playerConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.v(TAG, " > onServiceConnected");

            if(musicService == null){

                MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
                musicService = binder.getService();
                musicService.setTrackList(mTrackList);
                musicService.setTrack(selectedTrackPosition);

            }

            musicService.playTrack();

            if(!mBroadcastIsRegistered) {
                mActivity.registerReceiver(broadcastReceiver, new IntentFilter(
                        MusicService.BROADCAST_ACTION));

                mBroadcastIsRegistered = true;
            }

            //updateSeekBar();

            musicBound = true;

        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.v(TAG," > onServiceDisconnected");

            musicBound = false;
        }
    };

    @Override
    public void onPause() {
        // Unregister broadcast receiver
        if(mBroadcastIsRegistered) {
            mActivity.unregisterReceiver(broadcastReceiver);
            mBroadcastIsRegistered = false;
        }
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setOnDismissListener(null);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        mActivity.getApplicationContext().stopService(playIntent);
        mActivity.getApplicationContext().unbindService(playerConnection);
        musicService = null;
        super.onDestroy();
    }

    /** The system calls this only when creating the layout in a dialog. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    //------------------------------------------------------------------
    //Helpers
    //------------------------------------------------------------------

    private void updateTrackData(Intent intent) {
        String counter = intent.getStringExtra("counter");
        String mediamax = intent.getStringExtra("mediamax");
        String strSongEnded = intent.getStringExtra("song_ended");
        int seekProgress = Integer.parseInt(counter);

        seekMax = Integer.parseInt(mediamax);
        songEnded = Integer.parseInt(strSongEnded);
        mSeekBar.setMax(seekMax);
        mTrackEndTime.setText(Utils.getTimeString(seekMax));

        mSeekBar.setProgress(seekProgress);
        mTrackStartTime.setText(Utils.getTimeString(seekProgress));


        if (songEnded == 1) {
            mPlayPauseButton.setImageDrawable(getResources()
                    .getDrawable(android.R.drawable.ic_media_play));

            //TODO: go to next song instead?
        }



    }

    public void updateUI(int selectedTrackPosition){

        Track selectedTrack = mTrackList.get(selectedTrackPosition);

        mTrackTitle.setText(selectedTrack.getTrackName());
        mAlbumTitle.setText(selectedTrack.getAlbumName());

        Glide.with(mActivity)
                .load(selectedTrack.getAlbumThumbnailLarge())
                .error(R.drawable.track_default)
                .fitCenter()
                .into(mAlbumArt);

        if(musicService != null && musicService.isPlaying()){
            mPlayPauseButton.setImageDrawable(getResources()
                    .getDrawable(android.R.drawable.ic_media_pause));
        }
    }

//    public void updateSeekBar(final int currentTime, int maxDuration){
//
//        new android.os.Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (musicService != null && musicService.isPlaying()) {
//                    mTrackStartTime.setText();
//                }
//            }
//        }, 1000);
//
//    }



} //PlayBackFragment
