package br.com.gielamo.popularmovies.model.vo;

public enum ImageWidth {
    W92(92), W154(154), W185(185), W342(342), W500(500), W780(780), ORIGINAL(1000);

    private final int mPosterWidth;

    ImageWidth(int posterWidth) {
        this.mPosterWidth = posterWidth;
    }

    public static ImageWidth getProperImageWidth(int imageViewWidth) {
        ImageWidth imageWidth = null;
        ImageWidth[] allWidths = values();

        for (int i = 0; (i < allWidths.length) && (imageWidth == null); i++) {
            if (imageViewWidth <= allWidths[i].mPosterWidth) {
                imageWidth = allWidths[i];
            }
        }

        if (imageWidth == null) {
            imageWidth = ORIGINAL;
        }

        return imageWidth;
    }
}
