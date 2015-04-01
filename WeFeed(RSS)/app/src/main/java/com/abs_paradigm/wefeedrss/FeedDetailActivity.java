package com.abs_paradigm.wefeedrss;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.abs_paradigm.wefeedrss.FeedIO.FeedProviderIO;

import java.util.ArrayList;


/**
 * An activity representing a single Feed detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link MainActivity}.
 * <p/>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link FeedDetailFragment}.
 */
public class FeedDetailActivity extends ActionBarActivity implements FeedProviderIO.DatabaseListeners {

    private final String TAG = getClass().getSimpleName();

    private FeedProviderIO feedProviderIO;
    private Button btnRemoveFeed;
    private Button btnUpdateFeed;
    private String feedProviderUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_detail);

        feedProviderIO = FeedProviderIO.getInstance(this, getApplication());

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnRemoveFeed = (Button)findViewById(R.id.btnRemoveFeed);
        btnUpdateFeed = (Button)findViewById(R.id.btnUpdateFeed);

        try {
            Log.i(TAG, "Loading provider Url from bundle (Activity)");
            Intent intent = getIntent();
            feedProviderUrl = intent.getStringExtra("rssProvider");

            Log.i(TAG, "Provider: " + feedProviderUrl.toString());

        }catch (Exception e){

        }

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(FeedDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(FeedDetailFragment.ARG_ITEM_ID));
            FeedDetailFragment fragment = new FeedDetailFragment();

            arguments.putString("feedProviderUrl", feedProviderUrl);
            Log.i(TAG, "feedProviderUrl: "+ feedProviderUrl);

            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.feed_detail_container, fragment)
                    .commit();
        }

        initListeners();
    }

    private void initListeners(){
        final FeedDetailActivity classReference = this;
        btnRemoveFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick btnRemoveFeed");
                feedProviderIO.removeProvider(feedProviderUrl);

                navigateUpTo(new Intent(classReference, MainActivity.class));
            }
        });

        btnUpdateFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick btnUpdateFeed");
                feedProviderIO.getNewFeedsFromProvider(feedProviderUrl);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {

            navigateUpTo(new Intent(this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProvidersChange(ArrayList<String> providersList) {
    }

    @Override
    public void onFeedsChange() {

    }

}
