package com.example.pcurio.spotifystreamer;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pcurio.spotifystreamer.model.Artist;

import java.util.Collections;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewholder> {

    private Activity mActivity;

    private LayoutInflater inflater;
    private List<Artist> listItems = Collections.emptyList();

    private Utils.artistSelectionListener artistListener;

    public SearchAdapter
            (Activity activity, List<Artist> listItems, Utils.artistSelectionListener listener) {

        this.listItems = listItems;
        this.mActivity = activity;
        this.artistListener = listener;

        inflater = LayoutInflater.from(activity);
    }

    @Override
    public SearchAdapter.SearchViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.search_result_item, parent, false);
        SearchViewholder holder = new SearchViewholder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(SearchAdapter.SearchViewholder holder, int position) {

        Artist artist = listItems.get(position);

        holder.artistName.setText(artist.getArtistName());

        String artistThumbnail = artist.getArtistThumbnail();

        Glide.with(mActivity)
                .load(artistThumbnail)
                .error(R.drawable.artist_default)
                .centerCrop()
                .into(holder.artistThumbnail);
    }

    @Override
    public int getItemCount() {

        return listItems.size();

    }

    public class SearchViewholder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView artistThumbnail;
        private TextView artistName;

        public SearchViewholder(View itemView) {
            super(itemView);

            artistThumbnail = (ImageView) itemView.findViewById(R.id.search_album_art);
            artistName = (TextView) itemView.findViewById(R.id.search_artist_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            Artist selectedItem = listItems.get(getAdapterPosition());
            artistListener.onArtistClicked
                    (selectedItem.getSpotifyID(), selectedItem.getArtistName());
        }
    }
}
