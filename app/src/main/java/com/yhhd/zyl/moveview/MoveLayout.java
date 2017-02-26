package com.yhhd.zyl.moveview;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

/**
 * Author:Liang
 * Email:zhaoyongliangxny@qq.com
 * Created 2017年02月24日 10:08
 * Description:
 */

public class MoveLayout extends FrameLayout {
    boolean isFrist = true;
    private final DraggerCallBack callBack;
    private final ViewDragHelper mDragger;
    private SparseArray<ViewBean> mViewList = new SparseArray<>();


    public MoveLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        callBack = new DraggerCallBack();
        mDragger = ViewDragHelper.create(this, 1.0f, callBack);

    }



    private class  DraggerCallBack  extends ViewDragHelper.Callback{

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            //取得左边界的坐标
            final int leftBound = getPaddingLeft();
            //取得右边界的坐标
            final int rightBound = getWidth() - child.getWidth() - leftBound;
            //这个地方的含义就是 如果left的值 在leftbound和rightBound之间 那么就返回left
            //如果left的值 比 leftbound还要小 那么就说明 超过了左边界 那我们只能返回给他左边界的值
            //如果left的值 比rightbound还要大 那么就说明 超过了右边界，那我们只能返回给他右边界的值
            Log.i("zyl", "clampViewPositionHorizontal: "+left+"ddd"+leftBound+"fff"+rightBound);
            return Math.min(Math.max(left, leftBound), rightBound);
        }

        //纵向的注释就不写了 自己体会
        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            final int topBound = getPaddingTop();
            final int bottomBound = getHeight() - child.getHeight() - topBound;
            return Math.min(Math.max(top, topBound), bottomBound);
        }

        //需要子view的点击事件时添加，并且在xml文件中设置click属性为true
        @Override
        public int getViewHorizontalDragRange(View child) {
            return getMeasuredWidth() - child.getMeasuredWidth();
        }

        //需要子view的点击事件时添加，并且在xml文件中设置click属性为true
        @Override
        public int getViewVerticalDragRange(View child) {
            return getMeasuredHeight() - child.getMeasuredHeight();
        }

        @Override
        public boolean tryCaptureView(View child, int pointerId) {

            return true;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            try {
                int tag = (int) releasedChild.getTag();
                ViewBean ViewBean = mViewList.get(tag);
                int left = releasedChild.getLeft();
                int top = releasedChild.getTop();
                ViewBean.setTop(top);
                ViewBean.setLeft(left);
            }catch (Exception e){

            }



        }
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //决定是否拦截当前事件
        return mDragger.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //处理事件
        mDragger.processTouchEvent(event);
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);


        if (!isFrist) {
            for (int i = 0; i < mViewList.size(); i++) {
                int index = mViewList.keyAt(i);
                ViewBean ViewBean = mViewList.get(index);
                View view = ViewBean.getView();
                view.layout(ViewBean.getLeft(), ViewBean.getTop(), ViewBean.getLeft() + view.getMeasuredWidth()
                        , view.getMeasuredHeight() + ViewBean.top);

            }
        }
    }

    public void setView(View v,boolean isFrist,int i){
        ViewBean vBean = new ViewBean();
        vBean.setView(v);

        int left = v.getLeft();
        int top = v.getTop();
        vBean.setLeft(left);
         vBean.setTop(top);
        mViewList.put(i,vBean);
        this.isFrist = isFrist;
    }
    public void removeView(int  tag){
        mViewList.remove(tag);

    }

}
