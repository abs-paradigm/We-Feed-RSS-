package com.abs_paradigm.wefeedrss.FeedIO;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dom on 2015-03-28.
 */
public class FeedProviderIO implements FeedDownloaderTask.FeedCallBack{

    private final String TAG = getClass().getSimpleName();
    private final String FILENAME = "rssProviders.json";
    private String defaultRssProvider = "http://rss.cbc.ca/lineup/topstories.xml";

    private static ArrayList<DatabaseListeners> databaseListerners = new ArrayList<>();
    private ArrayList<String> rssProviderList = new ArrayList<>();
    private ArrayList<String> taskList = new ArrayList<>();

    Map<String, Feeds> feeds = new HashMap<>();

    private static ContextWrapper mContextWrapper;

    private static FeedProviderIO ourInstance = new FeedProviderIO();

    public static FeedProviderIO getInstance(DatabaseListeners feed, ContextWrapper contextWrapper) {
        databaseListerners.add(feed);
        mContextWrapper = contextWrapper;
        return ourInstance;
    }

    private FeedProviderIO(){

        updateAllFeeds();
    }

    public final void updateAllFeeds(){
        // For each supplier fetch the feeds
        for(String provider: rssProviderList){
            taskList.add(provider);

            new FeedDownloaderTask(this).execute(provider);
        }
    }

    private void notifyProvidersListeners(){
        for(DatabaseListeners databaseListener: databaseListerners){
            databaseListener.onProvidersChange(rssProviderList);
        }
    }

    private void notifyFeedsListeners(){
        for(DatabaseListeners databaseListener: databaseListerners){
            databaseListener.onFeedsChange();
        }
    }

    private JSONArray getJsonArrayFromFile(String filename, String arrayName){
        JSONArray rssProviderArray = new JSONArray();
        try {
            // Read the file
            String json = fileToString(filename);

            Log.i(TAG, json);

            // Create the json object
            JSONObject obj = new JSONObject(json);

            // Get the providers array from the JsonObject
            rssProviderArray = obj.optJSONArray(arrayName);
            return rssProviderArray;


        }catch (Exception e){
            Log.getStackTraceString(e);
        }
        return rssProviderArray;
    }

    public ArrayList<String> getProvidersList(){
        Log.e(TAG, "getProvidersList");

        try {

            JSONArray rssProviderArray = getJsonArrayFromFile(FILENAME, "rssProviders");

            // Check if the array is not null
            if (rssProviderArray == null) {

                return rssProviderList;
            }

            rssProviderList = new ArrayList<>();
            // Check
            for (int i = 0; i < rssProviderArray.length(); i++) {
                if(!rssProviderList.contains(rssProviderArray.getJSONObject(i).getString("url"))){
                    rssProviderList.add(rssProviderArray.getJSONObject(i).getString("url"));
                }
            }

        }catch (Exception e){
            Log.getStackTraceString(e);
        }
        return rssProviderList;
    }

    public void writeProvidersList(String url) {
        Log.e(TAG, "writeProvidersList: " + url);

        if (!rssProviderList.contains(url)) {
            rssProviderList.add(url);
        }

        try {

            JSONArray rssProviderArray = getJsonArrayFromFile(FILENAME, "rssProviders");
            Boolean alreadyInArray = false;

            if(!alreadyInArray){
                JSONObject newUrl = new JSONObject();
                newUrl.put("url", url);
                rssProviderArray.put(newUrl);

                JSONObject newDatabase = new JSONObject();
                newDatabase.put("rssProviders", rssProviderArray);
                // write to file
                writeJSON(FILENAME, newDatabase);
                notifyProvidersListeners();
            }
        }
        catch (Exception e){
            Log.getStackTraceString(e);
        }
    }

    public void overWriteProvidersList(ArrayList<String> providersList){
        Log.e(TAG, "overWriteProvidersList");

        createNewProvidersList();
        for(String provider: providersList){
            writeProvidersList(provider);
            notifyProvidersListeners();
        }
    }

    public void createNewProvidersList(){
        Log.e(TAG, "createNewProvidersList");

        try {
            // create a new file
            FileOutputStream fos = mContextWrapper.openFileOutput(FILENAME, Context.MODE_PRIVATE);

            JSONObject objNewFile = new JSONObject();
            JSONArray defaultProviderList = new JSONArray();

            // Add the default rssProvider to the json array
            JSONObject defaultProvider = new JSONObject();
            defaultProvider.put("url", defaultRssProvider);

            defaultProviderList.put(defaultProvider);
            objNewFile.put("rssProviders", defaultProviderList);
            fos.write(objNewFile.toString().getBytes());
            fos.close();

            if(!rssProviderList.contains(defaultRssProvider)){
                rssProviderList.add(defaultRssProvider);
            }

            notifyProvidersListeners();
        }catch (Exception e){
            Log.getStackTraceString(e);
        }
    }

    public void removeProvider(String providerUrl){
        Log.e(TAG, "removeProvider: " + providerUrl);

        // remove provider file
        try {

            FileOutputStream fos = mContextWrapper.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            JSONObject objNewFile = new JSONObject();

            //JSONArray providersArray = new JSONArray();
            JSONArray rssProviderArray = new JSONArray();
            Log.e(TAG, "rssProviderArray: " + rssProviderArray.toString());


            // remove from rssProviderList
            rssProviderList.remove(providerUrl);

            for(String provider: rssProviderList){
                rssProviderArray.put(new JSONObject().put("url", provider));
            }


            for(int i = 0; i < rssProviderArray.length(); i++){
                if(rssProviderArray.getJSONObject(i).getString("url") == providerUrl){
                    rssProviderArray.remove(i);
                    break;
                }
            }
            objNewFile.put("rssProviders", rssProviderArray);

            writeJSON(FILENAME, objNewFile);

            notifyProvidersListeners();
        }catch (Exception e){
            Log.getStackTraceString(e);

        }


        // delete any file related to that provider
        String filename = "" + providerUrl.hashCode();
        new File(filename).delete();

        notifyProvidersListeners();
    }

    public Feeds getFeedsFromProvider(String providerUrl){
        String filename = "" + providerUrl.hashCode();
        if(feeds.containsKey(filename)){

            return feeds.get(filename);
        }
        return new Feeds(providerUrl);

    }


    public Feeds getNewFeedsFromProvider(String providerUrl){
        Log.e(TAG, "getNewFeedsFromProvider");

        String filename = "" + providerUrl.hashCode();

        // Start a task to fetch the feeds from the provider
        // if no task is already fetching from the same provider
        if(!taskList.contains(filename)){
            Log.e(TAG, "Start a new task");

            taskList.add(providerUrl);
            new FeedDownloaderTask(this).execute(providerUrl);
        }else{
            if(feeds.containsKey(filename)){

                return feeds.get(filename);
            }
            return new Feeds(providerUrl);
        }

        //create a new Feeds
        Feeds newfeeds = new Feeds(providerUrl);

        if(fileExist(filename)){
            // load file
            Log.e(TAG, "fileExist");

            try {

                JSONArray feedsArray = getJsonArrayFromFile(filename, "feeds");
                // Check if the array is null
                if (feedsArray == null) {

                    return new Feeds(providerUrl);
                }

                for (int i = 0; i < feedsArray.length(); i++) {
                    JSONObject objFeed = feedsArray.getJSONObject(i);
                    newfeeds.addFeed(new Feed(objFeed.get("title").toString(),
                            objFeed.get("description").toString()));
                }

            }catch (Exception e){
                Log.getStackTraceString(e);
            }

            return newfeeds;

        }else{
            try {
                Log.e(TAG, "!fileExist");

                // create new file
                FileOutputStream fos = mContextWrapper.openFileOutput(filename, Context.MODE_PRIVATE);

                // Add item to json array
                JSONObject objNewFile = new JSONObject();

                JSONArray feedsArray = new JSONArray();

                objNewFile.put("provider", providerUrl);

                feedsArray.put(providerUrl);

                objNewFile.put("feeds", feedsArray);
                fos.write(objNewFile.toString().getBytes());
                fos.close();
            }catch (Exception e){
                Log.getStackTraceString(e);
            }

            return new Feeds(providerUrl);
        }
    }

    public Boolean fileExist(){
        Log.e(TAG, "fileExist");

        return fileExist(FILENAME);
    }

    private Boolean fileExist(String filename){
        try {
            File file = mContextWrapper.getBaseContext().getFileStreamPath(filename);

            return file.exists();
        }catch (Exception e){
            Log.getStackTraceString(e);

            return false;
        }
    }

    @Override
    public void feedNotificationUpdate(Feeds e) {
        String feedId = "" + e.getProvider().hashCode();
        if(!feeds.containsKey(feedId)){
            feeds.put(feedId, e);
        }
        taskList.remove(e.getProvider());
        notifyProvidersListeners();
    }

    private String fileToString(String fileName) {
        String json = "";
        InputStream is = null;
        try {
            is = mContextWrapper.openFileInput(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            json = new String(buffer, "UTF-8");

        } catch (Exception e) {
            e.printStackTrace();

        }

        return json;
    }

    private void writeJSON(String filename, JSONObject object) {

        try {
            FileOutputStream fos = mContextWrapper.openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(object.toString().getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface DatabaseListeners {
        /**
         * Callback for when an item has been selected.
         */
        public void onProvidersChange(ArrayList<String> providersList);

        public void onFeedsChange();
    }
}
