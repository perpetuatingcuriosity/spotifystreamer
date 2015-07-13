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

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.SearchViewholder> {

    private Activity mActivity;

    private LayoutInflater inflater;
    private List<ArtistListItem> listItems;

    private Utils.artistSelectionListener artistListener;

    public SearchListAdapter
            (Activity activity, List<ArtistListItem> listItems, Utils.artistSelectionListener listener) {

        this.listItems = listItems;
        this.mActivity = activity;
        this.artistListener = listener;

        inflater = LayoutInflater.from(activity);
    }

    @Override
    public SearchListAdapter.SearchViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.search_result_item, parent, false);

        SearchViewholder holder = new SearchViewholder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(SearchListAdapter.SearchViewholder holder, int position) {

        ArtistListItem artistListItem = listItems.get(position);

        holder.artistName.setText(artistListItem.getArtistName());

        String artistThumbnail = artistListItem.getArtistThumbnail();

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

    public class SearchViewholder extends RecyclerView.ViewHolder {

        private ImageView artistThumbnail;
        private TextView artistName;

        public SearchViewholder(View itemView) {
            super(itemView);

            artistThumbnail = (ImageView) itemView.findViewById(R.id.search_album_art);
            artistName = (TextView) itemView.findViewById(R.id.search_artist_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ArtistListItem selectedItem = listItems.get(getPosition());
                    artistListener.onArtistClicked
                            (selectedItem.getSpotifyID(), selectedItem.getArtistName());

                }
            });

        }
    }
}
