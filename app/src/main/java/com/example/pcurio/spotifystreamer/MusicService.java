/*Sources:
    Create a Music Player on Android by Sue Smith
    http://code.tutsplus.com/tutorials/create-a-music-player-on-android-song-playback--mobile-22778

    Android: Tutorials – Code a Background Music Player by Glowing Pigs
    http://www.glowingpigs.com/index.php/extras
    **NB: Tutorial 8 + 9 for seekbar functionality

*/

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

    private int selectedTrackPosition;

    //Seekbar processing
    private int currentPosition;
    private int totalTrackLength;
    private boolean trackEnded;
    public static final String BROADCAST_ACTION = "com.example.pcurio.spotifystreamer.seekprogress";

    Intent seekIntent;
    private final Handler handler = new Handler();

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

        selectedTrackPosition = 0;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        registerReceiver(seekProgressReceiver, new IntentFilter(PlaybackFragment.BROADCAST_SEEKBAR));
        return START_STICKY;

    }

    public void setTrackList(ArrayList<Track> trackList){
        tracks = trackList;
    }

    public void playTrack(){

        handler.removeCallbacks(sendUpdatesToUI);

        player.reset();
        Track track = tracks.get(selectedTrackPosition);

        try {
            player.setDataSource(getApplicationContext(), Uri.parse(track.getPreviewUrl()));
        } catch (IOException e) {
            Log.e(TAG, "Error setting Data source");

            Toast.makeText(this, getString(R.string.toast_music_service_player_error), Toast.LENGTH_SHORT).show();
        }
        player.prepareAsync();
    }

    //Seekbar handling
    //------------------------------------------------------------------

    public void setUpHandler(){
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 100);
    }

    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {

            sendTrackData();
            handler.postDelayed(this, 100);
        }
    };

    private void sendTrackData() {

        currentPosition = player.getCurrentPosition();
        totalTrackLength = player.getDuration();
        if(currentPosition <= totalTrackLength){

            if(currentPosition >= totalTrackLength - 100){
                trackEnded = true;
            }

            seekIntent.putExtra(Utils.CURRENT_TRACK_POSITION, currentPosition);
            seekIntent.putExtra(Utils.TRACK_LENGTH, totalTrackLength);
            seekIntent.putExtra(Utils.TRACK_ENDED, trackEnded);

            sendBroadcast(seekIntent);
        }
    }

    private BroadcastReceiver seekProgressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int seekPosition = intent.getIntExtra(Utils.SEEK_POSITION, 0);

            if (player.isPlaying()) {

                handler.removeCallbacks(sendUpdatesToUI);
                player.seekTo(seekPosition);
                setUpHandler();
            }
        }
    };

    //------------------------------------------------------------------

    public void setTrack(int trackIndex){
        selectedTrackPosition = trackIndex;
    }

    public int getTrack() {
        return selectedTrackPosition;
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

    //------------------------------------------------------------------

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        setUpHandler();
        trackEnded = false;

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        trackEnded = true;
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
        Toast.makeText(this, getString(R.string.toast_music_service_player_error), Toast.LENGTH_SHORT).show();
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
        unregisterReceiver(seekProgressReceiver);

        super.onDestroy();
    }
} //MusicService
