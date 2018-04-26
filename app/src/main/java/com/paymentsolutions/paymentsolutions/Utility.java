package com.paymentsolutions.paymentsolutions;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by Abdelrahman Hesham on 3/8/2018.
 */

public class Utility {
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }
}
