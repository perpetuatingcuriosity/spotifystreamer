package com.example.pcurio.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

public class ArtistListItem implements Parcelable {

    private String artistName;
    private String spotifyID;
    private String artistThumbnail;

    public ArtistListItem(){

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


    private ArtistListItem(Parcel in){
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

    public static final Parcelable.Creator<ArtistListItem> CREATOR = new Parcelable.Creator<ArtistListItem>() {
        @Override
        public ArtistListItem createFromParcel(Parcel in) {
            return new ArtistListItem(in);
        }

        @Override
        public ArtistListItem[] newArray(int size) {
            return new ArtistListItem[size];
        }
    };

} //ArtistListItem
