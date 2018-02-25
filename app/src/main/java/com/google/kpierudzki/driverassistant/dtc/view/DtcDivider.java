package com.google.kpierudzki.driverassistant.dtc.view;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.kpierudzki.driverassistant.App;
import com.google.kpierudzki.driverassistant.R;

/**
 * Created by Kamil on 25.12.2017.
 */

public class DtcDivider extends RecyclerView.ItemDecoration {

    private Drawable mDivider;

    public DtcDivider() {
        mDivider = ContextCompat.getDrawable(App.getAppContext(), R.drawable.dtc_divider);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);

        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);

            if ((parent.getChildAdapterPosition(child) == parent.getAdapter().getItemCount() - 1) && parent.getBottom() < bottom) { // this prevent a parent to hide the last item's divider
                parent.setPadding(parent.getPaddingLeft(), parent.getPaddingTop(), parent.getPaddingRight(), bottom - parent.getBottom());
            }

            mDivider.draw(c);
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);


    }
}
