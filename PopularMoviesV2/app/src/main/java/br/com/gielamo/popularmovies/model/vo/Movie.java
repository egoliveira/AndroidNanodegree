package br.com.gielamo.popularmovies.model.vo;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class Movie implements Parcelable, MovieInfo {
    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";

    @SerializedName("id")
    private long mId;

    @SerializedName("vote_count")
    private long mVoteCount;

    @SerializedName("vote_average")
    private BigDecimal mVoteAverage;

    @SerializedName("title")
    private String mTitle;

    @SerializedName("popularity")
    private BigDecimal mPopularity;

    @SerializedName("poster_path")
    private String mPosterPath;

    @SerializedName("backdrop_path")
    private String mBackdropPath;

    @SerializedName("overview")
    private String mOverview;

    @SerializedName("release_date")
    private Date mReleaseDate;

    @SerializedName("runtime")
    private BigInteger mRuntime;

    public Movie() {
    }

    private Movie(Parcel in) {
        this.mId = in.readLong();
        this.mVoteCount = in.readLong();
        this.mVoteAverage = (BigDecimal) in.readSerializable();
        this.mTitle = in.readString();
        this.mPopularity = (BigDecimal) in.readSerializable();
        this.mPosterPath = in.readString();
        this.mBackdropPath = in.readString();
        this.mOverview = in.readString();
        long tmpMReleaseDate = in.readLong();
        this.mReleaseDate = tmpMReleaseDate == -1 ? null : new Date(tmpMReleaseDate);
        this.mRuntime = (BigInteger) in.readSerializable();
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public long getVoteCount() {
        return mVoteCount;
    }

    public void setVoteCount(long voteCount) {
        this.mVoteCount = voteCount;
    }

    public BigDecimal getVoteAverage() {
        return mVoteAverage;
    }

    public void setVoteAverage(BigDecimal voteAverage) {
        this.mVoteAverage = voteAverage;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public BigDecimal getPopularity() {
        return mPopularity;
    }

    public void setPopularity(BigDecimal popularity) {
        this.mPopularity = popularity;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public void setPosterPath(String posterPath) {
        this.mPosterPath = posterPath;
    }

    public String getBackdropPath() {
        return mBackdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.mBackdropPath = backdropPath;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(String overview) {
        this.mOverview = overview;
    }

    public Date getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.mReleaseDate = releaseDate;
    }

    public String getPosterUrl(ImageWidth imageWidth) {
        String posterUrl = null;

        if ((mPosterPath != null) && (imageWidth != null)) {
            Uri.Builder builder = Uri.parse(IMAGE_BASE_URL).buildUpon();

            builder.appendPath(getImageWidthPathSegment(imageWidth));
            builder.appendEncodedPath(mPosterPath);

            posterUrl = builder.build().toString();
        }

        return posterUrl;
    }

    public String getBackdropUrl(ImageWidth imageWidth) {
        String backdropUrl = null;

        if ((mBackdropPath != null) && (imageWidth != null)) {
            Uri.Builder builder = Uri.parse(IMAGE_BASE_URL).buildUpon();

            builder.appendPath(getImageWidthPathSegment(imageWidth));
            builder.appendEncodedPath(mBackdropPath);

            backdropUrl = builder.build().toString();
        }

        return backdropUrl;
    }

    public BigInteger getRuntime() {
        return mRuntime;
    }

    public void setRuntime(BigInteger runtime) {
        this.mRuntime = runtime;
    }

    private String getImageWidthPathSegment(ImageWidth imageWidth) {
        String pathSegment;

        switch (imageWidth) {
            case W92:
                pathSegment = "w92";
                break;
            case W154:
                pathSegment = "w154";
                break;
            case W185:
                pathSegment = "w185";
                break;
            case W342:
                pathSegment = "w342";
                break;
            case W500:
                pathSegment = "w500";
                break;
            case W780:
                pathSegment = "w780";
                break;
            case ORIGINAL:
                pathSegment = "original";
                break;
            default:
                pathSegment = StringUtils.EMPTY;
                break;
        }

        return pathSegment;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mId);
        dest.writeLong(this.mVoteCount);
        dest.writeSerializable(this.mVoteAverage);
        dest.writeString(this.mTitle);
        dest.writeSerializable(this.mPopularity);
        dest.writeString(this.mPosterPath);
        dest.writeString(this.mBackdropPath);
        dest.writeString(this.mOverview);
        dest.writeLong(this.mReleaseDate != null ? this.mReleaseDate.getTime() : -1);
        dest.writeSerializable(mRuntime);
    }


    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
