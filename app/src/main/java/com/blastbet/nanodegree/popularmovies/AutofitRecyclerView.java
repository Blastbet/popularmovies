package com.blastbet.nanodegree.popularmovies;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by ilkka on 28.7.2016.
 * <p/>
 * RecyclerView extended with auto fit support. Auto fit extension
 * http://blog.sqisland.com/2014/12/recyclerview-autofit-grid.html
 */

public class AutofitRecyclerView extends RecyclerView {
    private static final String LOG_TAG = AutofitRecyclerView.class.getSimpleName();

    private GridLayoutManager mLayoutManager;
    private int mColumnWidth = -1;

    public AutofitRecyclerView(Context context) {
        super(context);
        init(context, null);
    }

    public AutofitRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AutofitRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            int[] attrsArray = {
                    android.R.attr.columnWidth
            };
            TypedArray array = context.obtainStyledAttributes(attrs, attrsArray);
            mColumnWidth = array.getDimensionPixelSize(0, -1);
            array.recycle();
        }

        mLayoutManager = new GridLayoutManager(context, 1);
        setLayoutManager(mLayoutManager);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        if (mColumnWidth > 0) {
            final int spanCount = (getMeasuredWidth() / mColumnWidth) + 1;

            mLayoutManager.setSpanCount(spanCount);

            //Log.v(LOG_TAG,  "onMeasure( " + widthSpec + ", " + heightSpec + " -> " + getMeasuredWidth() + "x" + getMeasuredHeight());
        }
    }


}
