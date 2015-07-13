package com.example.pcurio.spotifystreamer;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity implements Utils.artistSelectionListener {
    public static final String TAG = MainActivity.class.getSimpleName();

    private Context mContext;

    private SpotifyApi api;
    private SpotifyService spotify;

    private ArrayList<ArtistListItem> mSearchList = new ArrayList<>();
    private RecyclerView mSearchRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mSearchAdapter;

    private static final String SEARCH_LIST_KEY = "search_list";

    private TextView mEmptyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        //Spotify
        api = new SpotifyApi();
        spotify = api.getService();

        //RecyclerView
        mSearchRecyclerView = (RecyclerView) findViewById(R.id.searchRecyclerView);
        mLayoutManager = new LinearLayoutManager(mContext);
        mSearchAdapter = new SearchListAdapter(this, mSearchList, this);
        mSearchRecyclerView.setAdapter(mSearchAdapter);
        mSearchRecyclerView.setLayoutManager(mLayoutManager);
        mSearchRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mSearchRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        mSearchAdapter = new SearchListAdapter(this, mSearchList, this);
        mSearchRecyclerView.setAdapter(mSearchAdapter);

        mEmptyTextView = (TextView) findViewById(R.id.empty_main_no_artists);

        if(savedInstanceState != null && savedInstanceState.containsKey(SEARCH_LIST_KEY)) {
            mSearchList = savedInstanceState.getParcelableArrayList(SEARCH_LIST_KEY);

            mSearchAdapter = new SearchListAdapter(this, mSearchList, this);
            mSearchRecyclerView.setAdapter(mSearchAdapter);

        } else {
            //Get and handle intent
            Intent intent = getIntent();

            if(intent != null){
                handleIntent(intent);
            }
        }


    } //onCreate

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {

            //Get user’s query
            String query = intent.getStringExtra(SearchManager.QUERY);

            //Execute search for artists
            spotify.searchArtists(query, new Callback<ArtistsPager>() {
                @Override
                public void success(ArtistsPager artistsPager, Response response) {
                    Log.d(TAG, "searchArtists > success");
                    mSearchList.clear();

                    mEmptyTextView.setVisibility(View.GONE);

                    List<Artist> artists = artistsPager.artists.items;

                    if(artists.size() == 0) {
                        mSearchAdapter.notifyDataSetChanged();
                        mEmptyTextView.setVisibility(View.VISIBLE);

                        return;
                    }

                    for(Artist artist: artists){

                        ArtistListItem singleListItem = new ArtistListItem();
                        singleListItem.setSpotifyID(artist.id);
                        singleListItem.setArtistName(artist.name);

                        List<Image> albumArt = artist.images;

                        if(albumArt.size() > 0){
                            singleListItem.setArtistThumbnail(albumArt.get(0).url);
                        }

                        mSearchList.add(singleListItem);
                    }

                    mSearchAdapter.notifyDataSetChanged();

                } //success

                @Override
                public void failure(RetrofitError error) {
                    Log.d(TAG, "searchArtists > failure: ");

                    Toast.makeText(mContext, "Oops! There was a problem searching. " +
                            "Try your search again.", Toast.LENGTH_SHORT).show();

                } //failure
            });

        }
    } //handleIntent

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    } //onNewIntent

    @Override
    public void onArtistClicked(String spotifyID, String artistName) {

        Intent trackIntent = new Intent(MainActivity.this, TrackActivity.class);
        trackIntent.putExtra(Utils.ARTIST_NAME, artistName);
        trackIntent.putExtra(Utils.SPOTIFY_ID, spotifyID);
        startActivity(trackIntent);

    } //onArtistClicked

    //SaveInstanceState
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList(SEARCH_LIST_KEY, mSearchList);
        super.onSaveInstanceState(savedInstanceState);
    }

    //-----------------------------------------------------------------------------
    //MENU
    //-----------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
} //MainActivity
