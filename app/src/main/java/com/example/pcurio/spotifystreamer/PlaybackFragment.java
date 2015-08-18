package com.example.pcurio.spotifystreamer;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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

    private ArrayList<Track> mTrackList;
    private int selectedTrackPosition;

    //UI Elements
    private TextView mArtistName;
    private TextView mTrackTitle;
    private TextView mAlbumTitle;

    private ImageView mAlbumArt;

    private SeekBar mSeekBar;

    private ImageButton mPreviousButton;
    private ImageButton mPlayPauseButton;
    private ImageButton mNextButton;


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
                selectedTrackPosition = selectedTrackPosition--;
                musicService.setTrack(selectedTrackPosition);
                musicService.playTrack();

                updateUI(selectedTrackPosition);

            }
        });

        mPlayPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (musicService.isPlaying()) {
                    musicService.pauseTrack();

                    mPlayPauseButton.setImageDrawable(getResources()
                            .getDrawable(android.R.drawable.ic_media_play));

                } else {
                    musicService.resumeTrack();

                    mPlayPauseButton.setImageDrawable(getResources()
                            .getDrawable(android.R.drawable.ic_media_pause));
                }

            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedTrackPosition = selectedTrackPosition++;
                musicService.setTrack(selectedTrackPosition);
                musicService.playTrack();

                updateUI(selectedTrackPosition);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mActivity = getActivity();

        if(playIntent == null){
            playIntent = new Intent(mActivity, MusicService.class);

            mActivity.getApplicationContext().bindService(playIntent, playerConnection, Context.BIND_AUTO_CREATE);
            mActivity.startService(playIntent);
            Log.v(TAG, "Service started");
        }
    } //onActivityCreated

    //Connect to Music Player Service
    private ServiceConnection playerConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.v(TAG," > onServiceConnected");

            if(musicService == null){

                MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
                musicService = binder.getService();
                musicService.setTrackList(mTrackList);
                musicService.setTrack(selectedTrackPosition);
            }

            musicService.playTrack();

            musicBound = true;

        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.v(TAG," > onServiceDisconnected");

            musicBound = false;
        }
    };

    @Override
    public void onResume() {
        super.onResume();

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


    public void updateUI(int selectedTrackPosition){

        Track selectedTrack = mTrackList.get(selectedTrackPosition);

        mTrackTitle.setText(selectedTrack.getTrackName());
        mAlbumTitle.setText(selectedTrack.getAlbumName());

        Glide.with(mActivity)
                .load(selectedTrack.getAlbumThumbnailLarge())
                .error(R.drawable.track_default)
                .fitCenter()
                .into(mAlbumArt);
    }



} //PlayBackFragment
