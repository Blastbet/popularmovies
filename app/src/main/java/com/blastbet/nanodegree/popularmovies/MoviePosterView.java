package com.blastbet.nanodegree.popularmovies;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by ilkka on 28.12.2016.
 */

public class MoviePosterView extends ImageView {
    public MoviePosterView(Context context) {
        super(context);
    }

    public MoviePosterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MoviePosterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MoviePosterView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        // The poster width:height ratio is 2:3
        int height = (3 * width) / 2;
        setMeasuredDimension(width, height);
    }
}