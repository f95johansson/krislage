package com.uniaden.hackathon.outragous_kiwi.outragous_kiwi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Isak on 06/12/15.
 */
public class LiveFeedAdapter extends BaseAdapter {

    private ArrayList<LiveFeedData> liveData;
    private Context context;

    public LiveFeedAdapter(Context context) {
        liveData = new ArrayList<>();
        this.context = context;
        generateDataSet();

    }

    @Override
    public int getCount() {
        return liveData.size();
    }

    @Override
    public Object getItem(int position) {
        return liveData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parentV) {
        LiveFeedData data = liveData.get(position);
        View parent = LayoutInflater.from(context).
                inflate(R.layout.livefeed_data, parentV, false);

        ImageView icon = (ImageView)parent.findViewById(R.id.livefeed_icon);
        icon.setImageDrawable(context.getDrawable(data.iconId));

        TextView timeStamp = (TextView)parent.findViewById(R.id.livefeed_timestamp);
        timeStamp.setText(data.timeStamp);

        TextView text = (TextView)parent.findViewById(R.id.livefeed_text);
        text.setText(data.text);

        return parent;
    }

    private void generateDataSet(){
        for (int i = 0; i < 10; i++) {
            LiveFeedData data = new LiveFeedData();
            data.timeStamp = (3*i) + " minuter sedan";
            if(i % 2 == 0 && i < 7){
                data.iconId = R.drawable.polisen_logo_smallest;
            } else {
                data.iconId = R.drawable.msb_small;
            }
            data.text = "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.";
            liveData.add(data);
        }
    }
}
