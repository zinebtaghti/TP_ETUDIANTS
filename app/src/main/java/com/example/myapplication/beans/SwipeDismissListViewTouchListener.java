package com.example.myapplication.beans;

import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ListView;

public class SwipeDismissListViewTouchListener implements View.OnTouchListener {
    private ListView listView;
    private DismissCallbacks callbacks;
    private float downX;
    private int downPosition;
    private View downView;
    private boolean isSwiping;
    private VelocityTracker velocityTracker;
    private int slop;
    private int minFlingVelocity;
    private int maxFlingVelocity;

    // Interface pour callback de suppression et autre action
    public interface DismissCallbacks {
        boolean canDismiss(int position);
        void onDismiss(ListView listView, int position);
        void onOtherAction(ListView listView, int position);
    }

    public SwipeDismissListViewTouchListener(ListView listView, DismissCallbacks callbacks) {
        this.listView = listView;
        this.callbacks = callbacks;
        ViewConfiguration vc = ViewConfiguration.get(listView.getContext());
        slop = vc.getScaledTouchSlop();
        minFlingVelocity = vc.getScaledMinimumFlingVelocity();
        maxFlingVelocity = vc.getScaledMaximumFlingVelocity();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                downX = motionEvent.getRawX();
                downPosition = listView.pointToPosition((int) motionEvent.getX(), (int) motionEvent.getY());

                if (downPosition == ListView.INVALID_POSITION) {
                    return false;
                }

                downView = listView.getChildAt(downPosition - listView.getFirstVisiblePosition());
                velocityTracker = VelocityTracker.obtain();
                velocityTracker.addMovement(motionEvent);
                return false;

            case MotionEvent.ACTION_MOVE:
                velocityTracker.addMovement(motionEvent);
                float deltaX = motionEvent.getRawX() - downX;

                if (Math.abs(deltaX) > slop) {
                    isSwiping = true;
                    downView.setTranslationX(deltaX);
                }
                return false;

            case MotionEvent.ACTION_UP:
                if (isSwiping) {
                    float finalX = motionEvent.getRawX();
                    float deltaXFinal = finalX - downX;

                    if (Math.abs(deltaXFinal) > slop) {
                        if (deltaXFinal > 0) {  // Mouvement vers la droite
                            if (callbacks.canDismiss(downPosition)) {
                                callbacks.onDismiss(listView, downPosition);
                            }
                        } else {  // Mouvement vers la gauche
                            callbacks.onOtherAction(listView, downPosition);
                        }
                    }

                    downView.setTranslationX(0);
                    isSwiping = false;
                }
                return true;

            case MotionEvent.ACTION_CANCEL:
                if (velocityTracker != null) {
                    velocityTracker.recycle();
                    velocityTracker = null;
                }
                isSwiping = false;
                return false;
        }
        return false;
    }
}