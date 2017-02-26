package com.yhhd.zyl.moveview;

import android.view.View;

/**
 * Author:Liang
 * Email:zhaoyongliangxny@qq.com
 * Created 2017年02月24日 14:42
 * Description:
 */

public class ViewBean {
    public  int left;
    public  int top;
    public  float startTime;

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public int tag;
    public View view;

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }
}
