package com.google.kpierudzki.driverassistant.history.map.view;

import android.graphics.Point;
import android.support.animation.FloatPropertyCompat;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.SeekBar;

import com.google.kpierudzki.driverassistant.R;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;

/**
 * Created by Kamil on 02.09.2017.
 */

class SeekBarManager {

    private Point displaySize;
    private View customDrawer;
    private int fullMargin, noMargin;
    private SpringAnimation drawerAnimation;
    private SpringForce springForce;
    private VerticalSeekBar seekBar;

    SeekBarManager(@NonNull Display display, @NonNull View root, ICallbacks callbacks) {
        displaySize = new Point();
        display.getSize(displaySize);

        customDrawer = root.findViewById(R.id.custom_drawer);
        customDrawer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ConstraintLayout.LayoutParams clp = (ConstraintLayout.LayoutParams) customDrawer.getLayoutParams();
                clp.leftMargin = displaySize.x - customDrawer.getWidth();
                fullMargin = clp.leftMargin;
                noMargin = displaySize.x;
                customDrawer.setLayoutParams(clp);
                customDrawer.setVisibility(View.VISIBLE);
                customDrawer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        drawerAnimation = new SpringAnimation(customDrawer, new FloatPropertyCompat<View>("DrawerAnimation") {
            @Override
            public float getValue(View object) {
                return ((ConstraintLayout.LayoutParams) object.getLayoutParams()).leftMargin;
            }

            @Override
            public void setValue(View object, float value) {
                ConstraintLayout.LayoutParams clp = (ConstraintLayout.LayoutParams) object.getLayoutParams();
                clp.leftMargin = (int) value;
                object.setLayoutParams(clp);
            }
        });

        springForce = new SpringForce();
        springForce.setStiffness(SpringForce.STIFFNESS_MEDIUM);
        springForce.setDampingRatio(SpringForce.DAMPING_RATIO_NO_BOUNCY);
        drawerAnimation.setSpring(springForce);

        View grid = root.findViewById(R.id.grid);
        grid.setOnTouchListener(new View.OnTouchListener() {

            private float dx;
            private boolean reverseAnimationType = false;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.performClick();

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dx = motionEvent.getX();
                        drawerAnimation.cancel();
                        reverseAnimationType = true;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        reverseAnimationType = false;
                        float x = motionEvent.getX();

                        ConstraintLayout.LayoutParams clp = (ConstraintLayout.LayoutParams) customDrawer.getLayoutParams();
                        float left = clp.leftMargin + (x - dx);

                        //Keep boundaries
                        left = Math.min(displaySize.x, Math.max(displaySize.x - customDrawer.getWidth(), left));

                        clp.leftMargin = (int) left;
                        customDrawer.setLayoutParams(clp);
                        break;
                    case MotionEvent.ACTION_UP:
                        prepareFinalAnimationPositionAndStart(reverseAnimationType);
                        break;
                }
                return true;
            }
        });

        seekBar = root.findViewById(R.id.vertical_seekbar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                callbacks.onSeekBarChanged(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //...
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //...
            }
        });
    }

    public interface ICallbacks {
        void onSeekBarChanged(int progress);
    }

    private enum AnimationType {
        OPEN,
        CLOSE;

        public static AnimationType reverseType(AnimationType typeToReverse) {
            if (typeToReverse == AnimationType.CLOSE)
                return AnimationType.OPEN;
            else
                return AnimationType.CLOSE;
        }
    }

    private AnimationType getAnimationType() {
        ConstraintLayout.LayoutParams clp = (ConstraintLayout.LayoutParams) customDrawer.getLayoutParams();
        int currentMargin = clp.leftMargin;
        if (currentMargin > (fullMargin + (customDrawer.getWidth() * 0.5f))) {
            return AnimationType.CLOSE;
        } else {
            return AnimationType.OPEN;
        }
    }

    private void prepareFinalAnimationPositionAndStart(boolean reverseBehaviour) {
        AnimationType animationType = getAnimationType();
        switch (reverseBehaviour ? AnimationType.reverseType(animationType) : animationType) {
            case OPEN:
                springForce.setFinalPosition(fullMargin);
                break;
            case CLOSE:
                springForce.setFinalPosition(noMargin);
                break;
        }
        drawerAnimation.start();
    }

    public void setMax(int max) {
        seekBar.setMax(max);
    }
}
