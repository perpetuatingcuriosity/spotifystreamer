package com.example.pcurio.spotifystreamer;

public class Utils {

    public static final String SPOTIFY_ID = "spotifyID";
    public static final String ARTIST_NAME = "artistName";

    public static final String COUNTRY_CODE = "CA";

    public interface artistSelectionListener {
        void onArtistClicked(String spotifyID, String artistName);
    }

    //TODO: for use in Spotify Streamer pt. 2
    public interface trackSelectionListener {
        void onTrackClicked(String previewUrl);
    }

} //Utils
