package com.hosle.rowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by tanjiahao on 17/9/7.
 * .
 */

public class HoRowLayout extends ViewGroup {

    private ArrayList<Integer> rowsList = new ArrayList<>();
    private ArrayList<Integer> rowHeight = new ArrayList<>();

    public HoRowLayout(Context context) {
        super(context);
    }

    public HoRowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HoRowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //find the Maximum size of this layout
        int maxLayoutWidth = measureByMode(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int maxLayoutHeight = measureByMode(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();

        int countInRow = 0;
        int maxRowWidth = 0;
        int maxRowHeight = 0;
        int internalWidthMeasure = 0;
        int internalHeightMeasure = 0;
        rowsList.clear();
        rowHeight.clear();
        //measure the size of each child view
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            //measure and get the size of each child view
            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();
            //consider the margins of each child view
            MarginLayoutParams params = (MarginLayoutParams) childView.getLayoutParams();
            childWidth = childWidth + params.leftMargin + params.rightMargin;
            childHeight = childHeight + params.topMargin + params.bottomMargin;

            //compare the sum width of each row with the parent's constraint width
            if (maxRowWidth + childWidth > maxLayoutWidth) {
                internalWidthMeasure = Math.max(maxRowWidth, internalWidthMeasure);
                maxRowWidth = 0;
                internalHeightMeasure += maxRowHeight;
                rowHeight.add(maxRowHeight);
                maxRowHeight = 0;
                rowsList.add(countInRow);
                countInRow = 0;
            }
            maxRowWidth += childWidth;
            maxRowHeight = Math.max(maxRowHeight, childHeight);
            countInRow++;
        }
        //set the maximum width of all rows, and the sum height of all rows to the parent
        int newWidthSpec = MeasureSpec.makeMeasureSpec(internalWidthMeasure, MeasureSpec.getMode(widthMeasureSpec));
        int newHeightSpec = MeasureSpec.makeMeasureSpec(Math.max(internalHeightMeasure, maxLayoutHeight), MeasureSpec.getMode(heightMeasureSpec));

        // TODO: 17/9/7  don't forget the margin of the child view
        setMeasuredDimension(newWidthSpec, newHeightSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //layout each child view
        // 4,3,1,2 total 10
        int currentChild = 0;
        int rowWidth;
        int sumHeight = getPaddingTop();
        for (int row = 0; row < rowsList.size(); row++) {
            rowWidth = getPaddingLeft();
            for (int pos = 0; pos < rowsList.get(row); pos++) {
                View childView = getChildAt(currentChild);
                int childWidth = childView.getMeasuredWidth();
                int childHeight = childView.getMeasuredHeight();
                MarginLayoutParams params = (MarginLayoutParams) childView.getLayoutParams();

                childWidth = childWidth + params.rightMargin;
                childHeight = childHeight + params.bottomMargin;

                childView.layout(rowWidth + params.leftMargin, sumHeight + params.topMargin,
                        rowWidth + childWidth, sumHeight + childHeight);
                rowWidth = rowWidth + childWidth + params.leftMargin;
                currentChild++;
            }
            sumHeight += rowHeight.get(row);
        }

    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(),attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
    }

    private int measureByMode(int measureSpec) {
        switch (MeasureSpec.getMode(measureSpec)) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                return MeasureSpec.getSize(measureSpec);
            case MeasureSpec.UNSPECIFIED:
                break;
        }
        return 0;
    }
}
