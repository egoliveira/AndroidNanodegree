package br.com.gielamo.popularmovies.model.vo;

import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public abstract class PagedList<T extends Parcelable> {
    @SerializedName("page")
    private int mPage;

    @SerializedName("total_results")
    private int mTotalResults;

    @SerializedName("total_pages")
    private int mTotalPages;

    @SerializedName("results")
    private List<T> mResults;

    public int getPage() {
        return mPage;
    }

    public void setPage(int page) {
        this.mPage = page;
    }

    public int getTotalResults() {
        return mTotalResults;
    }

    public void setTotalResults(int totalResults) {
        this.mTotalResults = totalResults;
    }

    public int getTotalPages() {
        return mTotalPages;
    }

    public void setTotalPages(int totalPages) {
        this.mTotalPages = totalPages;
    }

    public List<T> getResults() {
        return mResults;
    }

    public void setResults(List<T> results) {
        this.mResults = results;
    }
}
