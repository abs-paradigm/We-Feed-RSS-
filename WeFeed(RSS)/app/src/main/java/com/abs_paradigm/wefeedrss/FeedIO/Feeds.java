package com.abs_paradigm.wefeedrss.FeedIO;

import java.util.ArrayList;

/**
 * Created by Dom on 2015-03-28.
 */
public class Feeds {

    private final String TAG = getClass().getSimpleName();

    private String provider;
    private ArrayList<Feed> feed = new ArrayList<>();

    public Feeds(){
    }

    public Feeds(String provider){
        this.provider = provider;
    }

    public Feeds(String provider, ArrayList<Feed> feed){
        this.provider = provider;
        this.feed = feed;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public ArrayList<Feed> getFeed() {
        return feed;
    }

    public void setFeed(ArrayList<Feed> feed) {
        this.feed = feed;
    }

    public void addFeed(Feed feed){this.feed.add(feed);}

}