package com.example.emojiworks.teamemoji;

import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.emojiworks.teamemoji.EmotionCursorAdapter;
import com.example.emojiworks.teamemoji.EmotionEditor;
import com.example.emojiworks.teamemoji.FrontScreen;
import com.example.emojiworks.teamemoji.R;
import com.example.emojiworks.teamemoji.data.LogDbContract;
import com.example.emojiworks.teamemoji.data.LogDbContract.LogDbEntry;
import com.example.emojiworks.teamemoji.data.LogDbHelper;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int URL_LOADER = 0;

    LogDbHelper mDpHelper;
    public static final String LOG_TAG = MainActivity.class.getName();
    static final String[] PROJECTION = new String[]{LogDbContract.LogDbEntry._ID, LogDbEntry.COLUMN_INVENTORY_NAME, LogDbEntry.COLUMN_INVENTORY_ACTIVITY, LogDbEntry.COLUMN_INVENTORY_WHERE, LogDbEntry.COLUMN_INVENTORY_WHOM, LogDbEntry.COLUMN_INVENTORY_DESCRIPTION, LogDbEntry.COLUMN_INVENTORY_TIME, LogDbEntry.COLUMN_INVENTORY_INTERVAL};

    EmotionCursorAdapter mCursorAdapter;
    static final Uri URI = LogDbContract.LogDbEntry.CONTENT_URI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EmotionEditor.class);
                startActivity(intent);
            }
        });
        Log.v(LOG_TAG, "Uri to watch" + "ON CREATE WAS CALLED");
        ListView inventoryListView = (ListView) findViewById(R.id.list_view_item);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        inventoryListView.setEmptyView(emptyView);
        mCursorAdapter = new EmotionCursorAdapter(this, null);
        inventoryListView.setAdapter(mCursorAdapter);


        inventoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long itemId) {
                Intent intent = new Intent(MainActivity.this, EmotionEditor.class);
                Uri currentPetUri = ContentUris.withAppendedId(LogDbContract.LogDbEntry.CONTENT_URI, itemId);
                intent.setData(currentPetUri);
                Log.v(LOG_TAG, "Uri to watch" + currentPetUri.toString());
                startActivity(intent);

            }
        });


        String[] project = {
                LogDbContract.LogDbEntry._ID,
                LogDbContract.LogDbEntry.COLUMN_INVENTORY_NAME};

        mDpHelper = new LogDbHelper(getApplicationContext());
        SQLiteDatabase db = mDpHelper.getReadableDatabase();
        Cursor cursor = db.query(LogDbContract.LogDbEntry.TABLE_NAME, project, null, null, null, null, null);

        if (cursor.getCount() < 1) {
            showFrontScreen(); //CREATED THIS FOR NA'S SCREEN
        }
        getLoaderManager().initLoader(URL_LOADER, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.v(LOG_TAG, "Uri to watch" + "ON START WAS CALLED");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch (i) {
            case URL_LOADER:
                // Returns a new CursorLoader
                return new CursorLoader(
                        this,   // Parent activity context
                        URI,        // Table to query
                        PROJECTION,     // Projection to return
                        null,            // No selection clause
                        null,            // No selection arguments
                        null             // Default sort order
                );
            default:
                // An invalid id was passed in
                return null;
        }

    }


    private void showFrontScreen() {
        DialogFragment newFragment = FrontScreen.newInstance();
        newFragment.show(getFragmentManager(), "dialog");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
        Log.v(LOG_TAG, "Uri to watch" + "ON LOADRESET WAS CALLED");

    }
}
