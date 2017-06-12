package com.fatchao.slideviewgroup;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * author pangchao
 * created on 2017/6/1
 * email fat_chao@163.com.
 */

public class SlideMenuItem extends ViewGroup {
    private int mTouchSlop;
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker = null;
    private int mMaxDistance;
    private int mLimit;
    private int mPointerId;
    private int mMaxVelocity;
    private PointF mTouchPoint;
    private PointF mInterPoint;
    private boolean isOpened;

    public SlideMenuItem(Context context) {
        super(context);
    }

    public SlideMenuItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mScroller = new Scroller(context);
        mMaxVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();

    }

    public SlideMenuItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams layoutParams) {
        return new MarginLayoutParams(layoutParams);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mMaxDistance = 0;//必须重置，onMeasure会执行多次
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int childCount = getChildCount();
        int childHeight = 0;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            if (i == 0) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                childHeight = child.getMeasuredHeight();
            } else {
                int heightSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY);
                measureChild(child, widthMeasureSpec, heightSpec);
            }
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            if (i > 0) {
                mMaxDistance += child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            }
        }
        mLimit = (int) (mMaxDistance * 0.45);
        setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthSize : getChildAt(0).getMeasuredWidth(), heightMode == MeasureSpec.EXACTLY ? heightSize : getChildAt(0).getMeasuredHeight());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int mLeftOffset = getPaddingLeft();
        int topOffset = getPaddingTop();
        for (int i = 0; i < childCount; i++) {
            View mChild = getChildAt(i);
            if (mChild.getVisibility() == GONE) {
                continue;
            }
            MarginLayoutParams lp = (MarginLayoutParams) mChild.getLayoutParams();
            mLeftOffset += lp.leftMargin;
            topOffset += lp.topMargin;
            int measuredWidth = mChild.getMeasuredWidth();
            int measuredHeight = mChild.getMeasuredHeight();
            mChild.layout(mLeftOffset, topOffset, mLeftOffset + measuredWidth, topOffset + measuredHeight);
            mLeftOffset += (measuredWidth + lp.rightMargin);
            topOffset = getPaddingTop();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean consume = false;
        acquireVelocityTracker(ev);
        if (mInterPoint == null)
            mInterPoint = new PointF();
        if (mTouchPoint == null)
            mTouchPoint = new PointF();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                consume = false;
                mInterPoint.set(ev.getRawX(), ev.getRawY());
                mTouchPoint.set(ev.getRawX(), ev.getRawY());
                mPointerId = ev.getPointerId(0);
                break;
            case MotionEvent.ACTION_MOVE:
                float abs = Math.abs(mInterPoint.x - ev.getRawX());
                if (Math.abs(abs) > mTouchSlop) {
                    consume = true;
                } else {
                    consume = isOpened;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isOpened && ev.getX() < getWidth() - getScrollX()) {
                    closeMenu();
                    consume = true;
                }
                break;
        }
        mInterPoint.set(ev.getRawX(), ev.getRawY());
        mTouchPoint.set(ev.getRawX(), ev.getRawY());
        return consume;
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                if (!mScroller.isFinished())
                    mScroller.abortAnimation();
                int variationX = (int) (mTouchPoint.x - ev.getRawX());
                int variationY = (int) (mTouchPoint.y - ev.getRawY());
                if (Math.abs(variationX) < Math.abs(variationY)) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    scrollBy(variationX, 0);
                    int scrollX = getScrollX();
                    if (scrollX > mMaxDistance)
                        scrollTo(mMaxDistance, 0);
                    if (scrollX < 0)
                        scrollTo(0, 0);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
                float velocityX = mVelocityTracker.getXVelocity(mPointerId);
                if (Math.abs(velocityX) > 1000) {
                    if (velocityX < -1000)
                        openMenu();
                    else
                        closeMenu();
                } else {
                    if (getScrollX() > mLimit)
                        openMenu();
                    else
                        closeMenu();
                }
                releaseVelocityTracker();
                break;
        }
        mTouchPoint.set(ev.getRawX(), ev.getRawY());
        return true;
    }

    private void acquireVelocityTracker(final MotionEvent event) {
        if (null == mVelocityTracker) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    private void openMenu() {
        isOpened = true;
        if (getScrollX() == mMaxDistance)
            return;
        mScroller.startScroll(getScrollX(), 0, mMaxDistance - getScrollX(), 0, 1000);
        invalidate();
    }

    private void closeMenu() {
        isOpened = false;
        if (getScrollX() == 0)
            return;
        mScroller.startScroll(getScrollX(), 0, -getScrollX(), 0, 1000);
        invalidate();

    }


    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }

    private void releaseVelocityTracker() {
        if (null != mVelocityTracker) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

}
