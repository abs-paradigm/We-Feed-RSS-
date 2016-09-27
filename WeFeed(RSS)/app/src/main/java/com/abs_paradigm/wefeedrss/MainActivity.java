package com.abs_paradigm.wefeedrss;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.abs_paradigm.wefeedrss.FeedIO.FeedProviderIO;

import java.util.ArrayList;

//
public class MainActivity extends ActionBarActivity implements FeedListFragment.Callbacks, FeedProviderIO.DatabaseListeners {

    private final String TAG = getClass().getSimpleName();
    private ArrayList<String> rssProviderList = new ArrayList<>();
    private String defaultRssProvider = "http://rss.cbc.ca/lineup/topstories.xml";
    private FeedProviderIO feedProviderIO;
    private Button btnAddFeed;
    private EditText edtAddFeed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAddFeed = (Button)findViewById(R.id.btnAddFeed);
        edtAddFeed = (EditText)findViewById(R.id.edtAddFeed);

        feedProviderIO = FeedProviderIO.getInstance(this, getApplication());

        String url = null;
        // Get url if the app is open by an intent
        try {
            Intent intent = getIntent();
            url = intent.getDataString();
            Log.i(TAG, url);

        } catch (Exception e) {
            Log.getStackTraceString(e);
        }

        try {
            // Check if the datebase exist
            if (feedProviderIO.fileExist()) {
                Log.i(TAG, "the file exist");

                if (url != null) {
                    // write the url to database
                    feedProviderIO.writeProvidersList(url);
                    Toast.makeText(getApplicationContext(), url, Toast.LENGTH_LONG).show();
                }

                rssProviderList = feedProviderIO.getProvidersList();
                Log.i(TAG, rssProviderList.toString());

            } else {
                Log.i(TAG, "the file doesn't exist");

                feedProviderIO.createNewProvidersList();

                if (url != null) {
                    // write the url to database
                    feedProviderIO.writeProvidersList(url);
                    Toast.makeText(getApplicationContext(), url, Toast.LENGTH_LONG).show();
                }
                rssProviderList.add(defaultRssProvider);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.feedContainer, new PlaceholderFragment())
                    .commit();
        }

        initListeners();
    }

    private final void initListeners(){
        btnAddFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = edtAddFeed.getText().toString();

                Toast.makeText(getApplicationContext(), url + " added", Toast.LENGTH_LONG).show();

                // TODO Check if the input is valid

                feedProviderIO.writeProvidersList(url);
            }
        });

        edtAddFeed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                btnAddFeed.setEnabled(edtAddFeed.getText().length() > 0);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(String id, String provider) {
        Log.i(TAG, "onItemSelected");

        Intent detailIntent = new Intent(this, FeedDetailActivity.class);
        detailIntent.putExtra(FeedDetailFragment.ARG_ITEM_ID, id);

        Bundle arguments = new Bundle();
        arguments.putString("rssProvider", provider);

        detailIntent.putExtras(arguments);
        startActivity(detailIntent);
    }

    @Override
    public void onProvidersChange(ArrayList<String> providersList) {
        Log.i(TAG, "onDatabaseChanged");

        //update the providers list
        rssProviderList = providersList;
        Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFeedsChange() {

    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_feed_list, container, false);
            return rootView;
        }
    }
}
