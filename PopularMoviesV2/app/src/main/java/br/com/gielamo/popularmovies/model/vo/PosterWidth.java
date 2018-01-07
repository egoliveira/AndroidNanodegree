package br.com.gielamo.popularmovies.model.vo;

public enum PosterWidth {
    W92(92), W154(154), W185(185), W342(342), W500(500), W780(780), ORIGINAL(1000);

    private final int mPosterWidth;

    PosterWidth(int posterWidth) {
        this.mPosterWidth = posterWidth;
    }

    public static PosterWidth getProperPosterWidth(int imageViewWidth) {
        PosterWidth posterWidth = null;
        PosterWidth[] allWidths = values();

        for (int i = 0; (i < allWidths.length) && (posterWidth == null); i++) {
            if (imageViewWidth <= allWidths[i].mPosterWidth) {
                posterWidth = allWidths[i];
            }
        }

        if (posterWidth == null) {
            posterWidth = ORIGINAL;
        }

        return posterWidth;
    }
}
