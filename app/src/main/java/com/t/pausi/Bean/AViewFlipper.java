package com.t.pausi.Bean;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ViewFlipper;

import com.t.pausi.R;



/**
 * Created by pintu22 on 11/12/17.
 */


public class AViewFlipper extends ViewFlipper
{

    Paint paint = new Paint();

    public AViewFlipper(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    protected void dispatchDraw(Canvas canvas)
    {
        super.dispatchDraw(canvas);
        int width = getWidth();

        float margin = 2;
        float radius = 5;
        float cx = width / 2-((radius + margin) * 2 * getChildCount() / 2);
        float cy = getHeight()-15;

        canvas.save();

        for (int i = 0; i < getChildCount(); i++)
        {
            if (i == getDisplayedChild())
            {
                paint.setColor(getResources().getColor(R.color.colorWhite));
                canvas.drawCircle(cx, cy, radius, paint);

            } else
            {
                paint.setColor(getResources().getColor(R.color.colorAccent));
                canvas.drawCircle(cx, cy, radius, paint);
            }
            cx += 2 * (radius + margin);
        }
        canvas.restore();
    }

}

