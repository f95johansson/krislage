package com.uniaden.hackathon.outragous_kiwi.outragous_kiwi;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Isak on 05/12/15.
 */
public class EventInformation implements Parcelable {

    private long time = System.currentTimeMillis();
    private String text = "Attached description for the event...";
    private String photo = "PhotoData";

    public EventInformation() {
    }

    public EventInformation(long time, String text, String photo) {
        this.time = time;
        this.text = text;
        this.photo = photo;
    }

    public EventInformation(Parcel in){
        time = in.readLong();
        text = in.readString();
        photo = in.readString();
    }

    public static final Creator<EventInformation> CREATOR = new Creator<EventInformation>() {
        @Override
        public EventInformation createFromParcel(Parcel in) {
            return new EventInformation(in);
        }

        @Override
        public EventInformation[] newArray(int size) {
            return new EventInformation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(time);
        dest.writeString(text);
        dest.writeString(photo);
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
