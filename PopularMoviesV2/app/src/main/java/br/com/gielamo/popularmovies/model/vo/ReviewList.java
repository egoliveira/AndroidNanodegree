package br.com.gielamo.popularmovies.model.vo;

import com.google.gson.annotations.SerializedName;

public class ReviewList extends PagedList<Review> {
    @SerializedName("id")
    private long mId;

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }
}
