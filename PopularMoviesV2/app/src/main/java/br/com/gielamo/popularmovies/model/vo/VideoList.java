package br.com.gielamo.popularmovies.model.vo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VideoList implements Parcelable {
    @SerializedName("id")
    private long mVideoId;

    @SerializedName("results")
    private List<Video> mVideos;

    public VideoList() {
    }

    private VideoList(Parcel in) {
        this.mVideoId = in.readLong();
        this.mVideos = in.createTypedArrayList(Video.CREATOR);
    }

    public long getVideoId() {
        return mVideoId;
    }

    public void setVideoId(long videoId) {
        this.mVideoId = videoId;
    }

    public List<Video> getVideos() {
        return mVideos;
    }

    public void setVideos(List<Video> videos) {
        this.mVideos = videos;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mVideoId);
        dest.writeTypedList(this.mVideos);
    }

    public static final Parcelable.Creator<VideoList> CREATOR = new Parcelable.Creator<VideoList>() {
        @Override
        public VideoList createFromParcel(Parcel source) {
            return new VideoList(source);
        }

        @Override
        public VideoList[] newArray(int size) {
            return new VideoList[size];
        }
    };
}
