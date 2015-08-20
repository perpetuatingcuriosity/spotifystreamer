package com.example.pcurio.spotifystreamer;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.example.pcurio.spotifystreamer.model.Track;

import java.io.IOException;
import java.util.ArrayList;

public class MusicService extends Service implements MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {
    public static final String TAG = MusicService.class.getSimpleName();

    private MediaPlayer player;
    private final IBinder musicBind = new MusicBinder();

    private ArrayList<Track> tracks;

    private int songPosition;

    //Seekbar processing
    int mediaPosition;
    int mediaMax;
    private final Handler handler = new Handler();
    private static int songEnded;
    public static final String BROADCAST_ACTION = "com.example.pcurio.spotifystreamer.seekprogress";

    Intent seekIntent;

    //------------------------------------------------------------------

    public void onCreate(){
        super.onCreate();

        player = new MediaPlayer();

        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
        player.setOnSeekCompleteListener(this);

        //Set up intent for seekbar broadcast
        seekIntent = new Intent(BROADCAST_ACTION);
        songPosition = 0;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        registerReceiver(broadcastReceiver, new IntentFilter(PlaybackFragment.BROADCAST_SEEKBAR));
        return START_STICKY;

    }

    public void setTrackList(ArrayList<Track> trackList){
        tracks = trackList;
    }

    public void playTrack(){

        player.reset();

        Track track = tracks.get(songPosition);
        Log.v(TAG, "PLAY TRACK: " + track.getTrackName() + " POS: " + songPosition);

        try {
            player.setDataSource(getApplicationContext(), Uri.parse(track.getPreviewUrl()));
        } catch (IOException e) {
            Log.e(TAG, "Error setting Data source");
        }

        player.prepareAsync();

        setUpHandler();
    }

    //Seekbar handling
    //------------------------------------------------------------------

    public void setUpHandler(){
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 1000);
    }

    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            LogMediaPosition();
            handler.postDelayed(this, 1000);
        }
    };

    private void LogMediaPosition() {
        if (player.isPlaying()) {
            mediaPosition = player.getCurrentPosition();
//             if (mediaPosition < 1) {
//             Toast.makeText(this, "Buffering...", Toast.LENGTH_SHORT).show();
//             }
            mediaMax = player.getDuration();
            //seekIntent.putExtra("time", new Date().toLocaleString());
            seekIntent.putExtra("counter", String.valueOf(mediaPosition));
            seekIntent.putExtra("mediamax", String.valueOf(mediaMax));
            seekIntent.putExtra("song_ended", String.valueOf(songEnded));
            sendBroadcast(seekIntent);
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int seekPos = intent.getIntExtra("seekpos", 0);
            if (player.isPlaying()) {
                handler.removeCallbacks(sendUpdatesToUI);
                player.seekTo(seekPos);
                setUpHandler();

            }
        }
    };

    //------------------------------------------------------------------

    public void setTrack(int trackIndex){
        Log.v(TAG, "SET TRACK, SONG POSITION: " + songPosition);
        songPosition = trackIndex;
    }

    public int getTrack() {
        Log.v(TAG, "GET TRACK, SONG POSITION: " + songPosition);
        return songPosition;
    }

    public void pauseTrack(){
        player.pause();
    }

    public void resumeTrack(){
        player.start();
    }

    public boolean isPlaying(){
        return player.isPlaying();
    }

    public int getCurrentPosition(){
        return player.getCurrentPosition();
    }

    public void setPlayPosition(int seekPosition){
        player.seekTo(seekPosition);
    }

    //------------------------------------------------------------------

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        Toast.makeText(this, "There was a problem starting the player", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {

        if (!mediaPlayer.isPlaying()){
            player.start();
        }
    }

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(sendUpdatesToUI);
        // Unregister seekbar receiver
        unregisterReceiver(broadcastReceiver);

        super.onDestroy();
    }
} //MusicService
