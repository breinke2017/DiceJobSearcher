package com.ekniernairb.dicejobsearcher.staticClasses;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by brian.reinke on 9/6/2017.
 */

public class ScaledDisplay {

    public enum ScaledDensity {
        DP,
        SP,
        PX;
    }

    public static int dpToPx1(Context context, ScaledDensity scaledDensity, int scaledUnitSize) {
        int complexUnitValue;

        switch (scaledDensity) {
            case DP:
                complexUnitValue = TypedValue.COMPLEX_UNIT_DIP;
                break;
            case SP:
                complexUnitValue = TypedValue.COMPLEX_UNIT_SP;
                break;
            case PX:
                complexUnitValue = TypedValue.COMPLEX_UNIT_PX;
                break;
            default:
                complexUnitValue = TypedValue.COMPLEX_UNIT_PX;
        }

        return (int) TypedValue.applyDimension(complexUnitValue, scaledUnitSize,
                context.getResources().getDisplayMetrics());
    }
}

