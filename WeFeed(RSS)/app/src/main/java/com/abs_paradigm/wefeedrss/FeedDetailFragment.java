package com.abs_paradigm.wefeedrss;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.abs_paradigm.wefeedrss.FeedIO.Feed;
import com.abs_paradigm.wefeedrss.FeedIO.FeedProviderIO;
import com.abs_paradigm.wefeedrss.FeedIO.Feeds;
import com.abs_paradigm.wefeedrss.dummy.DummyContent;

import java.util.ArrayList;

/**
 * A fragment representing a single Feed detail screen.
 * This fragment is either contained in a
 * in two-pane mode (on tablets) or a {@link FeedDetailActivity}
 * on handsets.
 */
public class FeedDetailFragment extends Fragment implements FeedProviderIO.DatabaseListeners{

    private final String TAG = getClass().getSimpleName();

    private String feedProviderUrl = "";
    private Feed feed;
    private Feeds feeds = new Feeds();
    private ListView feed_list;
    private FeedProviderIO feedProviderIO;


    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FeedDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        feedProviderIO = FeedProviderIO.getInstance(this, getActivity().getApplication());

        try {
            feedProviderUrl = getArguments().getString("feedProviderUrl");

        }catch (Exception e){
            Log.getStackTraceString(e);
        }
        feeds = feedProviderIO.getFeedsFromProvider(feedProviderUrl);

        feedProviderIO.getNewFeedsFromProvider(feedProviderUrl);

        if (getArguments().containsKey(ARG_ITEM_ID)) {

            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            //((TextView) rootView.findViewById(R.id.feed_detail)).setText(mItem.content);
            feed_list = (ListView)rootView.findViewById(R.id.feed_detail);

            feed_list.setAdapter(new TwoLineAdapter(getActivity(), feeds));
        }

        return rootView;
    }

    @Override
    public void onProvidersChange(ArrayList<String> providersList) {
        Log.i(TAG, "onDatabaseChanged");

        feeds = feedProviderIO.getFeedsFromProvider(feedProviderUrl);
        feed_list.setAdapter(new TwoLineAdapter(getActivity(), feeds));
    }

    @Override
    public void onFeedsChange() {
        Log.i(TAG, "OnFeedsChange");

        feeds = feedProviderIO.getFeedsFromProvider(feedProviderUrl);
        feed_list.setAdapter(new TwoLineAdapter(getActivity(), feeds));
    }
}
