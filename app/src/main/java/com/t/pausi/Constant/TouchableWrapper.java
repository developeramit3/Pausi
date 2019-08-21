package com.t.pausi.Constant;

/**
 * Created by technorizen on 15/2/19.
 */
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.t.pausi.Fragment.HomeFragment;


public class TouchableWrapper extends FrameLayout {

    public TouchableWrapper(Context context) {
        super(context);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                Log.e("DOWN_TOUCH"," DD");
                HomeFragment.removePoly(event);

                break;
                case MotionEvent.ACTION_MOVE:
                    Log.e("MOVE_TOUCH"," DD");
                    HomeFragment.addPolyline(event);
                break;

            case MotionEvent.ACTION_UP:
                Log.e("UP_TOUCH"," DD");
                HomeFragment.close_polyline();
                break;
        }
        return super.dispatchTouchEvent(event);
    }
}