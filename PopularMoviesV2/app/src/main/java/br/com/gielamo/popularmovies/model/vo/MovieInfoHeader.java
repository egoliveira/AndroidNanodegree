package br.com.gielamo.popularmovies.model.vo;

import android.os.Parcel;

public class MovieInfoHeader implements MovieInfo {
    private final String mTitle;

    public MovieInfoHeader(String title) {
        this.mTitle = title;
    }

    private MovieInfoHeader(Parcel in) {
        this.mTitle = in.readString();
    }

    public String getTitle() {
        return mTitle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mTitle);
    }

    public static final Creator<MovieInfoHeader> CREATOR = new Creator<MovieInfoHeader>() {
        @Override
        public MovieInfoHeader createFromParcel(Parcel source) {
            return new MovieInfoHeader(source);
        }

        @Override
        public MovieInfoHeader[] newArray(int size) {
            return new MovieInfoHeader[size];
        }
    };
}
