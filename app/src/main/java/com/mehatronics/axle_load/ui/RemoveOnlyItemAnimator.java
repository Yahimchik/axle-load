package com.mehatronics.axle_load.ui;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

public class RemoveOnlyItemAnimator extends DefaultItemAnimator {

    public RemoveOnlyItemAnimator() {
        setRemoveDuration(250); // по умолчанию ~120-250мс
    }

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        dispatchAddFinished(holder);
        return false;
    }

    @Override
    public boolean animateMove(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        dispatchMoveFinished(holder);
        return false;
    }

    @Override
    public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder,
                                 int fromLeft, int fromTop, int toLeft, int toTop) {
        dispatchChangeFinished(oldHolder, true);
        if (oldHolder != newHolder) {
            dispatchChangeFinished(newHolder, false);
        }
        return false;
    }
}