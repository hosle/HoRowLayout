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

    /*
    * Storage of the number of child views in each row
    */
    private ArrayList<Integer> rowsList = new ArrayList<>();
    /*
    * Storage of the maximum height of each row.
    */
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
        /*
        * find the Maximum size of this layout and consider its padding
        */
        int maxLayoutWidth = measureByMode(widthMeasureSpec);
        int maxLayoutHeight = measureByMode(heightMeasureSpec);

        int countInRow = 0;
        int rowWidth = 0;
        int rowHeight = 0;
        int internalWidthMeasure = 0;
        int internalHeightMeasure = 0;
        this.rowsList.clear();
        this.rowHeight.clear();
        /*
        * To measure all children's size by parent's method.
        */
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        /*
        * To measure each child view in traversal.
        */
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            /*
            * The size of each child view after being measured.
            */
            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();
            /*
            * Considering the margins of each child view.
            */
            MarginLayoutParams params = (MarginLayoutParams) childView.getLayoutParams();
            childWidth = childWidth + params.leftMargin + params.rightMargin;
            childHeight = childHeight + params.topMargin + params.bottomMargin;
            /*
            * 1. Comparing the sum of width in each row with the parent's constraint width.
            * 2. The height and the numbers of the children in this row should be stored in case of adding will be
            * the next child view will exceed the constraint width.
            * 3. Update the size measurement of all added children.
            * 4. reset the variable of the row, including rowWidth and rowHeight
            * */
            if (rowWidth + childWidth > maxLayoutWidth - getPaddingLeft() - getPaddingRight()) {
                internalWidthMeasure = Math.max(rowWidth, internalWidthMeasure);
                rowWidth = 0;
                internalHeightMeasure += rowHeight;
                this.rowHeight.add(rowHeight);
                rowHeight = 0;
                rowsList.add(countInRow);
                countInRow = 0;
            }
            rowWidth += childWidth;
            rowHeight = Math.max(rowHeight, childHeight);
            countInRow++;
        }
        /*
        * Set the maximum width of all rows, and the sum height of all rows to the parent
        */
        int newWidthSpec = MeasureSpec.makeMeasureSpec(internalWidthMeasure + getPaddingLeft() + getPaddingRight(),
                MeasureSpec.getMode(widthMeasureSpec));
        int newHeightSpec = MeasureSpec.makeMeasureSpec(Math.min(internalHeightMeasure + getPaddingTop() + getPaddingBottom(), maxLayoutHeight),
                MeasureSpec.getMode(heightMeasureSpec));

        setMeasuredDimension(newWidthSpec, newHeightSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int childIndex = 0;
        int rowWidth;
        int sumHeight = getPaddingTop();
        for (int row = 0; row < rowsList.size(); row++) {
            rowWidth = getPaddingLeft();
            for (int pos = 0; pos < rowsList.get(row); pos++) {
                View childView = getChildAt(childIndex);
                int childWidth = childView.getMeasuredWidth();
                int childHeight = childView.getMeasuredHeight();
                /*
                * Considering the margin of each child
                */
                MarginLayoutParams params = (MarginLayoutParams) childView.getLayoutParams();
                childWidth = childWidth + params.rightMargin;
                childHeight = childHeight + params.bottomMargin;
                /*
                * The left is the current rowWidth
                * The top is the sum height of all rows above this.
                * Call layout() to layout this child view.
                */
                childView.layout(rowWidth + params.leftMargin, sumHeight + params.topMargin,
                        rowWidth + childWidth, sumHeight + childHeight);
                rowWidth = rowWidth + childWidth + params.leftMargin;
                childIndex++;
            }
            sumHeight += rowHeight.get(row);
        }
    }

    /*
    * Override the following 3 methods to fetch the margin params of each child view.
    */
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
