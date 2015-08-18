package com.example.pcurio.spotifystreamer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.example.pcurio.spotifystreamer.model.Track;

import java.io.IOException;
import java.util.ArrayList;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    public static final String TAG = MusicService.class.getSimpleName();

    private MediaPlayer player;
    private final IBinder musicBind = new MusicBinder();

    private ArrayList<Track> tracks;

    private int songPosition;

    public void onCreate(){
        //create the service
        super.onCreate();

        songPosition = 0;
        player = new MediaPlayer();

        setUpPlayer();
    }

    public void setUpPlayer(){

        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void setTrackList(ArrayList<Track> trackList){
        tracks = trackList;
    }

    public void playTrack(){

        player.reset();

        Track track = tracks.get(songPosition);

        try {
            player.setDataSource(getApplicationContext(), Uri.parse(track.getPreviewUrl()));
        } catch (IOException e) {
            Log.e(TAG, "Error setting Data source");
        }

        player.prepareAsync();

    }

    public void setTrack(int trackIndex){
        songPosition = trackIndex;
    }

    public int getTrack(){
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

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        // ... react appropriately ...
        // The MediaPlayer has moved to the Error state, must be reset!
        return false;
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

} //MusicService
