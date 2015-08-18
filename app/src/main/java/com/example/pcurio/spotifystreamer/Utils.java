package com.example.pcurio.spotifystreamer;

import com.example.pcurio.spotifystreamer.model.Track;

import java.util.ArrayList;

public class Utils {

    public static final String SPOTIFY_ID = "spotifyID";
    public static final String ARTIST_NAME = "artistName";

    public static final String TRACK_LIST = "trackList";
    public static final String SELECTED_TRACK_POSITION = "selectedTrack";

    public static final String COUNTRY_CODE = "CA";

    public interface artistSelectionListener {
        void onArtistClicked(String spotifyID, String artistName);
    }

    public interface trackSelectionListener {
        void onTrackClicked(ArrayList<Track>trackList, int trackPosition);
    }

} //Utils
