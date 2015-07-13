package com.example.pcurio.spotifystreamer;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.TrackViewholder> {

    private Activity mActivity;

    private LayoutInflater inflater;
    private List<ArtistTrackItem> listItems;

    private Utils.trackSelectionListener trackListener;

    public TrackListAdapter
            (Activity activity, List<ArtistTrackItem> listItems, Utils.trackSelectionListener listener) {

        this.listItems = listItems;
        this.mActivity = activity;
        this.trackListener = listener;

        inflater = LayoutInflater.from(activity);
    }

    @Override
    public TrackViewholder onCreateViewHolder(ViewGroup parent, int i) {
        View view = inflater.inflate(R.layout.track_result_item, parent, false);

        TrackViewholder holder = new TrackViewholder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(TrackViewholder holder, int position) {

        ArtistTrackItem artistTrackItem = listItems.get(position);

        holder.trackName.setText(artistTrackItem.getTrackName());
        holder.albumName.setText(artistTrackItem.getAlbumName());

        String albumThumbnail = artistTrackItem.getAlbumThumbnailLarge();

            Glide.with(mActivity)
                    .load(albumThumbnail)
                    .error(R.drawable.track_default)
                    .centerCrop()
                    .into(holder.albumThumbnail);
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class TrackViewholder extends RecyclerView.ViewHolder {

        private ImageView albumThumbnail;
        private TextView trackName;
        private TextView albumName;

        public TrackViewholder(View itemView) {
            super(itemView);

            albumThumbnail = (ImageView) itemView.findViewById(R.id.artist_album_art);
            trackName = (TextView) itemView.findViewById(R.id.artist_track_name);
            albumName = (TextView) itemView.findViewById(R.id.artist_album_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //TODO: Spotify Streamer pt. 2
                    ArtistTrackItem selectedItem = listItems.get(getPosition());
                    trackListener.onTrackClicked(selectedItem.getPreviewUrl());

                }
            });

        }
    }

}
