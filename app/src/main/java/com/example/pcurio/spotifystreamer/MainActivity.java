package com.example.pcurio.spotifystreamer;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

public class MainActivity extends AppCompatActivity implements SearchFragment.TrackListener {
    public static final String TAG = MainActivity.class.getSimpleName();

    public static final String SEARCH_FRAGMENT_TAG = "SFTAG";
    public static final String TRACK_FRAGMENT_TAG = "TFTAG";

    private Context mContext;

    private SearchFragment mSearchFragment;
    private TrackFragment mTrackFragment;

    private SpotifyApi api;
    private SpotifyService spotify;

    private ArrayList<ArtistListItem> mSearchList = new ArrayList<>();

    private boolean mTwoPane;

    //------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        //Spotify
        api = new SpotifyApi();
        spotify = api.getService();

        mSearchFragment = new SearchFragment();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.search_fragment_container, mSearchFragment, SEARCH_FRAGMENT_TAG)
                .commit();

        //Get and handle intent
        Intent intent = getIntent();

        if(intent != null) {
            handleIntent(intent);
        }

        if(findViewById(R.id.track_fragment_container)!= null) {
            mTwoPane = true;

            mTrackFragment = new TrackFragment();

            if(savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.track_fragment_container, mTrackFragment,
                                TRACK_FRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }

    } //onCreate

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {

            //Get user’s query
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchArtist(query);
        }
    } //handleIntent

    public void searchArtist(String query){
        //Execute search for artist
        spotify.searchArtists(query, new Callback<ArtistsPager>() {
            @Override
            public void success(ArtistsPager artistsPager, Response response) {
                Log.d(TAG, "searchArtists > success");
                mSearchList.clear();

                List<Artist> artists = artistsPager.artists.items;

                for (Artist artist : artists) {

                    ArtistListItem singleListItem = new ArtistListItem();
                    singleListItem.setSpotifyID(artist.id);
                    singleListItem.setArtistName(artist.name);

                    List<Image> albumArt = artist.images;

                    if (albumArt.size() > 0) {
                        singleListItem.setArtistThumbnail(albumArt.get(0).url);
                    }

                    mSearchList.add(singleListItem);
                }

                mSearchFragment.updateArtistResults(mSearchList);

            } //success

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "searchArtists > failure: ");

                Toast.makeText(mContext, "Oops! There was a problem searching. " +
                        "Try your search again.", Toast.LENGTH_SHORT).show();

            } //failure
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    } //onNewIntent

    @Override
    public void displayTracks(String spotifyID, String artistName) {

        if(mTwoPane){

            Bundle b = new Bundle();
            b.putString("id", spotifyID);
            b.putString("artist", artistName);

            mTrackFragment = new TrackFragment();
            mTrackFragment.setArguments(b);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.track_fragment_container, mTrackFragment,
                            TRACK_FRAGMENT_TAG)
                    .commit();

        } else {

            Intent trackIntent = new Intent(MainActivity.this, TrackActivity.class);
            trackIntent.putExtra(Utils.ARTIST_NAME, artistName);
            trackIntent.putExtra(Utils.SPOTIFY_ID, spotifyID);
            startActivity(trackIntent);
        }

    } //displayTracks


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

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchArtist(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

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
