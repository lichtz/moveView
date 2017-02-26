package com.yhhd.zyl.moveview.mapping;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by hellpro on 2017/2/26.
 */

public class MyImageView  extends ImageView{
    public MyImageView(Context context) {
        super(context);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}
