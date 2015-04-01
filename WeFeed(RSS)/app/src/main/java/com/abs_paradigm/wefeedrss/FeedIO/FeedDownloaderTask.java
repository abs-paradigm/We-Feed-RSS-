package com.abs_paradigm.wefeedrss.FeedIO;

import android.os.AsyncTask;
import android.util.Log;


/**
 * Created by Dom on 2015-03-28.
 */
class FeedDownloaderTask extends AsyncTask<String, Void, Feeds> {

    private final String TAG = getClass().getSimpleName();

    private static final int SIM_NETWORK_DELAY = 5000;
    private static final boolean HAS_NETWORK_CONNECTION = true;

    private FeedCallBack mParentActivityFeedBack;
    //private String provider;

    public FeedDownloaderTask(FeedCallBack feedCallBack) {
        super();

        mParentActivityFeedBack = feedCallBack;
    }

    @Override
    protected Feeds doInBackground(String... params) {
        Log.i(TAG, "doInBackground");

        //provider = params[0];
        return fetchFeeds(params);
    }

    private Feeds fetchFeeds(String[] feedSupplierUrl){
        Log.i(TAG, "fetchFeeds: " + feedSupplierUrl[0]);

        XMLHandler xmlHandler;
        Feed feed;
        Feeds feeds = new Feeds(feedSupplierUrl[0]);

        xmlHandler = new XMLHandler(feedSupplierUrl[0]);
        xmlHandler.fetchXML();
        while(xmlHandler.parsingComplete);

        int limit = xmlHandler.getTitle().size() < xmlHandler.getDescriptions().size() ? xmlHandler.getTitle().size(): xmlHandler.getDescriptions().size();
        for(int i = 0; i < limit; i++){
            feed = new Feed(xmlHandler.getTitle().get(i), xmlHandler.getDescriptions().get(i));
            feeds.addFeed(feed);
        }

        return feeds;
    }

    @Override
    protected void onPostExecute(Feeds result) {
        super.onPostExecute(result);

            mParentActivityFeedBack.feedNotificationUpdate(result);
    }


    public interface FeedCallBack {

        public void feedNotificationUpdate(Feeds e);
    }
}
