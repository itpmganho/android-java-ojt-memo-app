package com.example.e01_memo.util;

import android.app.Activity;
import android.graphics.Point;
import android.view.Display;

public class DisplayUtil {

    public static Point getDisplaySize(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        return point;
    }
}
