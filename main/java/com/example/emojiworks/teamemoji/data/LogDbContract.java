package com.example.emojiworks.teamemoji.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


public class LogDbContract {
    private LogDbContract(){}

    public static final class LogDbEntry implements BaseColumns{
        public static final String CONTENT_AUTHORITY = "com.example.emojiworks.teamemoji";
        public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
        public static final String PATH_INVENTORY = "emoji";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

        public final static String TABLE_NAME = "emoji";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_INVENTORY_NAME = "name";
        public final static String COLUMN_INVENTORY_ACTIVITY = "activity";
        public final static String COLUMN_INVENTORY_WHERE = "wheredb";
        public final static String COLUMN_INVENTORY_WHOM = "whom";
        public final static String COLUMN_INVENTORY_DESCRIPTION = "description";
        public final static String COLUMN_INVENTORY_TIME = "time";
        public final static String COLUMN_INVENTORY_INTERVAL = "interval";


        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;
    }
}
