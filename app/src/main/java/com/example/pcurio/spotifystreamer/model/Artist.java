package com.example.pcurio.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Artist implements Parcelable {

    private String artistName;
    private String spotifyID;
    private String artistThumbnail;

    public Artist(){

    }

    public void setArtistName(String name){
        artistName = name;
    }

    public String getArtistName(){
        return artistName;
    }

    public void setSpotifyID(String id){
        spotifyID = id;
    }

    public String getSpotifyID(){
        return spotifyID;
    }

    public void setArtistThumbnail(String url){
        artistThumbnail = url;
    }

    public String getArtistThumbnail(){
        return artistThumbnail;
    }


    private Artist(Parcel in){
        this.artistName = in.readString();
        this.spotifyID = in.readString();
        this.artistThumbnail = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(this.artistName);
        parcel.writeString(this.spotifyID);
        parcel.writeString(this.artistThumbnail);

    }

    public static final Parcelable.Creator<Artist> CREATOR = new Parcelable.Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel in) {
            return new Artist(in);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };

} //ArtistListItem
