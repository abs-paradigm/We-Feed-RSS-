package com.abs_paradigm.wefeedrss;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.TwoLineListItem;

import com.abs_paradigm.wefeedrss.FeedIO.Feeds;

class TwoLineAdapter extends BaseAdapter {

    private final String TAG = getClass().getSimpleName();

    private Context context;
    private Feeds feeds;

    public TwoLineAdapter(Context context, Feeds feeds) {
        this.context = context;
        this.feeds = feeds;
    }

    @Override
    public int getCount() {

        return feeds.getFeed().size();
    }

    @Override
    public Object getItem(int position) {

        return feeds.getFeed().get(position);
    }

    @Override
    public long getItemId(int position) {

        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TwoLineListItem twoLineListItem;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            twoLineListItem = (TwoLineListItem) inflater.inflate(
                    android.R.layout.simple_list_item_2, null);
        } else {
            twoLineListItem = (TwoLineListItem) convertView;
        }

        TextView text1 = twoLineListItem.getText1();
        TextView text2 = twoLineListItem.getText2();

        text1.setText(feeds.getFeed().get(position).getTitle());
        text2.setText("" + feeds.getFeed().get(position).getDescription());

        return twoLineListItem;
    }
}
