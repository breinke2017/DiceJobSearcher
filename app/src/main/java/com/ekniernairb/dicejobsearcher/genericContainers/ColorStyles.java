package com.ekniernairb.dicejobsearcher.genericContainers;

/**
 * Created by brian.reinke on 9/6/2017.
 */

/*
* Generic class full of pre-chosen color styles work have tested well with the app.
* */


/*
* ColorStyles will be a list of pre-chosen values that have tested well.  These values will be passed in via constructor.
* */
public class ColorStyles {
    private int mResId;
    private String mColorPrimary;
    private String mColorPrimaryDark;
    private String mColorCardView;
    private String mColorCardViewText;

    public ColorStyles(int resId, String colorPrimary, String colorPrimaryDark, String colorCardView, String colorCardViewText) {
        this.mResId = resId;
        this.mColorPrimary = colorPrimary;
        this.mColorPrimaryDark = colorPrimaryDark;
        this.mColorCardView = colorCardView;
        this.mColorCardViewText = colorCardViewText;
    }


    public String getColorPrimary() {
        return mColorPrimary;
    }

    public String getColorCardView() {
        return mColorCardView;
    }

    public String getColorCardViewText() {
        return mColorCardViewText;
    }

}
