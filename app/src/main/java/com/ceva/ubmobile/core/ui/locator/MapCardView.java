package com.ceva.ubmobile.core.ui.locator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by brian on 04/07/2017.
 */

public class MapCardView extends android.support.v7.widget.CardView {
    public MapCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }
}
