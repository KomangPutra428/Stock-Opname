package com.tvip.stockopnamegudang;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class SlowListView extends ListView {
    private static final float SCROLL_RATIO = 0f; // Adjust this value to control scroll speed

    private float lastY;

    public SlowListView(Context context) {
        super(context);
    }

    public SlowListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SlowListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaY = ev.getRawY() - lastY;
                lastY = ev.getRawY();

                // Adjust scroll speed by multiplying deltaY with SCROLL_RATIO
                deltaY *= SCROLL_RATIO;

                MotionEvent modifiedEvent = MotionEvent.obtain(ev.getDownTime(), ev.getEventTime(), ev.getAction(), ev.getX(), ev.getY() + deltaY, ev.getMetaState());
                return super.dispatchTouchEvent(modifiedEvent);
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

}

