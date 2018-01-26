package br.com.gielamo.popularmovies.view.detail;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import br.com.gielamo.popularmovies.R;
import br.com.gielamo.popularmovies.model.vo.MovieInfo;
import br.com.gielamo.popularmovies.model.vo.Video;

public class MovieVideoViewHolder extends MovieInfoViewHolder {
    private static final String THUMBNAIL_URL_TEMPLATE = "https://img.youtube.com/vi/%1$s/hqdefault.jpg";

    private final ImageView mThumbnail;

    private final TextView mTitle;

    public MovieVideoViewHolder(View itemView) {
        super(itemView);

        mThumbnail = itemView.findViewById(R.id.movie_video_item_view_thumbnail);
        mTitle = itemView.findViewById(R.id.movie_video_item_view_title);
    }

    @Override
    public void bind(MovieInfo movieInfo) {
        if (movieInfo instanceof Video) {
            Video video = (Video) movieInfo;

            Picasso.with(mThumbnail.getContext()).load(String.format(THUMBNAIL_URL_TEMPLATE, video.getVideoKey())).into(mThumbnail);

            mTitle.setText(video.getName());
        }
    }
}
