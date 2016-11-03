package com.blastbet.nanodegree.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by ilkka on 3.11.2016.
 */

public class PosterGridLayoutManager extends GridLayoutManager implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String LOG_TAG = PosterGridLayoutManager.class.getSimpleName();

    private int mPosterWidth;
    private Context mContext;

    public PosterGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public PosterGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
        init(context);
    }

    public PosterGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
        init(context);
    }

    private void init(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        mPosterWidth = Integer.parseInt(
                preferences.getString(context.getString(R.string.pref_poster_size_key),
                        context.getString(R.string.pref_poster_size_default))
        );

        preferences.registerOnSharedPreferenceChangeListener(this);
        mContext = context;
    }

    private void updateSpanCount() {
        final int spanCount = Math.max(1, (getWidth() / mPosterWidth));
        setSpanCount(spanCount);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        updateSpanCount();
        super.onLayoutChildren(recycler, state);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        final String prefWidthKey = mContext.getString(R.string.pref_poster_size_key);
        if (key == prefWidthKey) {
            mPosterWidth = Integer.parseInt(
                    sharedPreferences.getString(prefWidthKey,
                            mContext.getString(R.string.pref_poster_size_default))
            );
            Log.v(LOG_TAG, "Changing poster width parameter.");
        }
    }
}
