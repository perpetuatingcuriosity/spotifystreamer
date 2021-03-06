package com.example.pcurio.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Track implements Parcelable {

    private String trackName;
    private String albumName;
    private String albumThumbnailLarge;
    private String albumThumbnailSmall;
    private String previewUrl;

    public Track(){

    }

    public void setTrackName(String name){
        trackName = name;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumThumbnailLarge(String largeUrl) {
        this.albumThumbnailLarge = largeUrl;
    }

    public String getAlbumThumbnailLarge() {
        return albumThumbnailLarge;
    }

    public void setAlbumThumbnailSmall(String smallUrl) {
        this.albumThumbnailSmall = smallUrl;
    }

    public String getAlbumThumbnailSmall() {
        return albumThumbnailSmall;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }


    private Track(Parcel in){
        this.trackName = in.readString();
        this.albumName = in.readString();
        this.albumThumbnailLarge = in.readString();
        this.albumThumbnailSmall = in.readString();
        this.previewUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.trackName);
        parcel.writeString(this.albumName);
        parcel.writeString(this.albumThumbnailLarge);
        parcel.writeString(this.albumThumbnailSmall);
        parcel.writeString(this.previewUrl);
    }

    public static final Parcelable.Creator<Track> CREATOR = new Parcelable.Creator<Track>() {
        @Override
        public Track createFromParcel(Parcel in) {
            return new Track(in);
        }

        @Override
        public Track[] newArray(int size) {
            return new Track[size];
        }
    };

} //ArtistTrackItem
