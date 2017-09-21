package com.ekniernairb.dicejobsearcher.staticClasses;

/**
 * Created by brian.reinke on 9/6/2017.
 */

import android.content.Context;

import com.ekniernairb.dicejobsearcher.R;
import com.ekniernairb.dicejobsearcher.genericContainers.ColorStyles;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by brian.reinke on 8/6/2017.
 */

public class ColorSchemeButtons {
    public static int colorButtonSelected;
    public static boolean hasSelectedColor;

    // Color Button Schemes
    public static final int NUMBER_OF_BUTTONS=4;
    public static List<ColorStyles> colorStylesList = new ArrayList<ColorStyles>(NUMBER_OF_BUTTONS);

    /*
    * Sets the Activities' theme.  Note: While technically not part of the Activity theme, also am including
    * CardView and the TextView objects to change too.  They are logically part of this same task.
    * */
    public static void setActivityTheme(Context context, int colorButtonSelected) {
        // must be called before any Activity instantiation!
        switch (colorButtonSelected) {
            case 0:
                context.setTheme(R.style.ColorSchemeButton0);
                break;
            case 1:
                context.setTheme(R.style.ColorSchemeButton1);
                break;
            case 2:
                context.setTheme(R.style.ColorSchemeButton2);
                break;
            case 3:
                context.setTheme(R.style.ColorSchemeButton3);
                break;
        }
    }

}


