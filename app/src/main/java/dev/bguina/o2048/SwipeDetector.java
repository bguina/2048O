package dev.bguina.o2048;

import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;


public class SwipeDetector implements View.OnTouchListener {
    private int mMinFlingVelocity;
    private int mMaxFlingVelocity;

    // Fixed properties
    private View mView;
    private int mViewWidth = 1; // 1 and not 0 to prevent dividing by zero

    // Transient properties
    private float mDownX;
    private float mDownY;
    private SwipeListener mHandler;
    private VelocityTracker mVelocityTracker;

    public SwipeDetector(View view, SwipeListener handler) {
        ViewConfiguration vc = ViewConfiguration.get(view.getContext());
        mMinFlingVelocity = vc.getScaledMinimumFlingVelocity() * 4;
        mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
        mView = view;
        mHandler = handler;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        if (mViewWidth < 2) {
            mViewWidth = mView.getWidth();
        }

        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                mDownX = motionEvent.getRawX();
                mDownY = motionEvent.getRawY();
                mVelocityTracker = VelocityTracker.obtain();
                mVelocityTracker.addMovement(motionEvent);
                return false;
            }

            case MotionEvent.ACTION_UP: {
                if (mVelocityTracker == null)
                    break;

                float deltaX = motionEvent.getRawX() - mDownX;
                float deltaY = motionEvent.getRawY() - mDownY;

                mVelocityTracker.addMovement(motionEvent);
                mVelocityTracker.computeCurrentVelocity(1000);

                float velocityX = mVelocityTracker.getXVelocity();
                float velocityY = mVelocityTracker.getYVelocity();
                float absVelocityX = Math.abs(velocityX);
                float absVelocityY = Math.abs(velocityY);

                if (absVelocityX > absVelocityY) {
                    if ((velocityX < 0) == (deltaX < 0) && mMinFlingVelocity <= absVelocityX && absVelocityX <= mMaxFlingVelocity) {
                        if (absVelocityX == velocityX)
                            mHandler.onSwipe(SwipeListener.RIGHT);
                        else
                            mHandler.onSwipe(SwipeListener.LEFT);
                    }
                } else {
                    if ((velocityY < 0) == (deltaY < 0) && mMinFlingVelocity <= absVelocityY && absVelocityY <= mMaxFlingVelocity) {
                        if (absVelocityY == velocityY)
                            mHandler.onSwipe(SwipeListener.DOWN);
                        else
                            mHandler.onSwipe(SwipeListener.UP);
                    }
                }

                mVelocityTracker.recycle();
                mVelocityTracker = null;
                mDownX = 0;
                mDownY = 0;
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                if (mVelocityTracker == null) {
                    break;
                }

                mVelocityTracker.recycle();
                mVelocityTracker = null;
                mDownX = 0;
                mDownY = 0;
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                if (mVelocityTracker == null) {
                    break;
                }

                mVelocityTracker.addMovement(motionEvent);
                break;
            }
        }
        return false;
    }
}
