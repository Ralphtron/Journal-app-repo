package com.example.emojiworks.teamemoji;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by ralphinator on 12/13/2016.
 */

public class EmotionCursorAdapter extends CursorAdapter {

    public EmotionCursorAdapter (Context context, Cursor c){
        super(context, c, 0 /* flags */);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

    }
    /**
     *
     * @return takes values from the database and places them into our xml elements
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        if(cursor.isFirst()){
            view.setLayoutParams(new AbsListView.LayoutParams(-1,1));
            view.setVisibility(View.GONE);
        }
        TextView tvBody = (TextView) view.findViewById(R.id.name);
        TextView tvPriority = (TextView) view.findViewById(R.id.description);
        TextView tvActivity = (TextView) view.findViewById(R.id.activities);
        // Extract properties from cursor
        String body = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        String priority = cursor.getString(cursor.getColumnIndexOrThrow("description"));
        String activity = cursor.getString(cursor.getColumnIndexOrThrow("activity"));


        // Populate fields with extracted properties
        tvBody.setText(body);
        tvPriority.setText(priority);
        tvActivity.setText(activity);
    }
}