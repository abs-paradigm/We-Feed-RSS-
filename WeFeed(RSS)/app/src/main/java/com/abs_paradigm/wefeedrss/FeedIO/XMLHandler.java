package com.abs_paradigm.wefeedrss.FeedIO;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


class XMLHandler {

    private final String TAG = getClass().getSimpleName();

    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<String> descriptions = new ArrayList<>();

    private String urlString = null;
    public volatile boolean parsingComplete = true;

    private XmlPullParserFactory xmlFactoryObject;
    public XMLHandler(String url){
        this.urlString = url;
    }

    public ArrayList<String> getTitle(){
        return titles;
    }
    public ArrayList<String> getDescriptions(){
        return descriptions;
    }


    private void parseXMLAndStoreIt(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.i(TAG, "parseXMLAndStoreIt");

        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            if (name.equalsIgnoreCase("item")) {

                readFeed(parser);
                continue;
            }

        }
        parsingComplete = false;
    }


    private void readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.i(TAG, "readFeed");

        String text = null;
        int event = parser.getEventType();
        Boolean isFeedComplete = false;
        while (!isFeedComplete) {

            String name = parser.getName();

            switch (event){

                case XmlPullParser.START_TAG:
                    break;
                case XmlPullParser.TEXT:
                    text = parser.getText();

                    break;
                case XmlPullParser.END_TAG:
                    if(name.equals("title")){

                        titles.add(text);
                    }

                    else if(name.equals("description")){

                        descriptions.add(android.text.Html.fromHtml(text).toString());
                        isFeedComplete = true;
                    }

                    break;
            }
            event = parser.next();
        }
    }

    public void fetchXML(){
        Log.i(TAG, "fetchXML: " + urlString);

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    // Starts the query
                    conn.connect();
                    InputStream stream = conn.getInputStream();
                    xmlFactoryObject = XmlPullParserFactory.newInstance();
                    XmlPullParser myparser = xmlFactoryObject.newPullParser();
                    myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);

                    myparser.setInput(stream, null);
                    myparser.nextTag();
                    parseXMLAndStoreIt(myparser);
                    stream.close();
                } catch (Exception e) {
                    Log.getStackTraceString(e);
                }
            }
        });
        thread.start();
    }
}
