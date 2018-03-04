package br.com.gielamo.popularmovies.model.vo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Video implements Parcelable, MovieInfo {
    @SerializedName("id")
    private String mId;

    @SerializedName("iso_639_1")
    private String mLanguageCode;

    @SerializedName("iso_3166_1")
    private String mCountryCode;

    @SerializedName("key")
    private String mVideoKey;

    @SerializedName("name")
    private String mName;

    @SerializedName("site")
    private String mSite;

    @SerializedName("size")
    private int mSize;

    @SerializedName("type")
    private String mType;

    public Video() {
    }

    private Video(Parcel in) {
        this.mId = in.readString();
        this.mLanguageCode = in.readString();
        this.mCountryCode = in.readString();
        this.mVideoKey = in.readString();
        this.mName = in.readString();
        this.mSite = in.readString();
        this.mSize = in.readInt();
        this.mType = in.readString();
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getLanguageCode() {
        return mLanguageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.mLanguageCode = languageCode;
    }

    public String getCountryCode() {
        return mCountryCode;
    }

    public void setCountryCode(String countryCode) {
        this.mCountryCode = countryCode;
    }

    public String getVideoKey() {
        return mVideoKey;
    }

    public void setVideoKey(String videoKey) {
        this.mVideoKey = videoKey;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getSite() {
        return mSite;
    }

    public void setSite(String site) {
        this.mSite = site;
    }

    public int getSize() {
        return mSize;
    }

    public void setSize(int size) {
        this.mSize = size;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        this.mType = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mId);
        dest.writeString(this.mLanguageCode);
        dest.writeString(this.mCountryCode);
        dest.writeString(this.mVideoKey);
        dest.writeString(this.mName);
        dest.writeString(this.mSite);
        dest.writeInt(this.mSize);
        dest.writeString(this.mType);
    }

    public static final Parcelable.Creator<Video> CREATOR = new Parcelable.Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel source) {
            return new Video(source);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };
}
