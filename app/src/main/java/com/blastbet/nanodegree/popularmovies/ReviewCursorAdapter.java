package com.blastbet.nanodegree.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by ilkka on 3.11.2016.
 */

public class ReviewCursorAdapter extends CursorRecyclerViewAdapter<ReviewCursorAdapter.ViewHolder> {

    public ReviewCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewAuthor;
        TextView textViewContent;

        public ViewHolder(View rootView) {
            super(rootView);
            textViewAuthor = (TextView) rootView.findViewById(R.id.text_review_author);
            textViewContent = (TextView) rootView.findViewById(R.id.text_review_content);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        final View v = LayoutInflater.from(context).inflate(R.layout.review_list_item, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        viewHolder.textViewAuthor.setText(cursor.getString(MovieDetailsFragment.COL_REVIEW_AUTHOR));
        viewHolder.textViewContent.setText(cursor.getString(MovieDetailsFragment.COL_REVIEW_CONTENT));
    }
}