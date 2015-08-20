package com.example.pcurio.spotifystreamer;

import com.example.pcurio.spotifystreamer.model.Track;

import java.util.ArrayList;

public class Utils {


    //API Constants
    public static final String SPOTIFY_ID = "spotifyID";
    public static final String ARTIST_NAME = "artistName";

    public static final String TRACK_LIST = "trackList";
    public static final String SELECTED_TRACK_POSITION = "selectedTrack";

    public static final String COUNTRY_CODE = "CA";


    //Selection Listeners
    public interface artistSelectionListener {
        void onArtistClicked(String spotifyID, String artistName);
    }

    public interface trackSelectionListener {
        void onTrackClicked(ArrayList<Track>trackList, int trackPosition);
    }


    //Media Player Constants


    //Media Player Helpers
    public static String getTimeString(long millis) {
        StringBuffer buf = new StringBuffer();

        int hours = (int) (millis / (1000 * 60 * 60));
        int minutes = (int) ((millis % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) (((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);

        buf
                .append(String.format("%02d", hours))
                .append(":")
                .append(String.format("%02d", minutes))
                .append(":")
                .append(String.format("%02d", seconds));

        return buf.toString();
    }

} //Utils
