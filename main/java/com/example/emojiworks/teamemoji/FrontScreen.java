package com.example.emojiworks.teamemoji;

import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.emojiworks.teamemoji.data.LogDbContract;
import com.example.emojiworks.teamemoji.data.LogDbContract.LogDbEntry;
import com.example.emojiworks.teamemoji.data.LogDbHelper;


/**
 * Created by ralphinator on 5/19/2017.
 */

public class FrontScreen extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private LogDbHelper mDbHelper;
    private static final int URL_LOADER = 0;
    private Button submitBtn;
    private Button cancelBtn;
    private android.view.animation.Animation anim;
    private EmotionCursorAdapter mCursorAdapter;
    /**
     *
     * @return this is the way the login screen is called and implemented from @MainActivity
     */
    public static FrontScreen newInstance() {
        FrontScreen frag = new FrontScreen();
        return frag;
    }

    private EditText nameEditText;
    static final String[] PROJECTION = new String[] {LogDbContract.LogDbEntry._ID, LogDbEntry.COLUMN_INVENTORY_NAME, LogDbEntry.COLUMN_INVENTORY_ACTIVITY, LogDbEntry.COLUMN_INVENTORY_WHERE, LogDbEntry.COLUMN_INVENTORY_WHOM, LogDbEntry.COLUMN_INVENTORY_DESCRIPTION, LogDbEntry.COLUMN_INVENTORY_TIME, LogDbEntry.COLUMN_INVENTORY_INTERVAL};
    static final Uri URI = LogDbEntry.CONTENT_URI;


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.login_fragment, null);

        /**
         *
         * @return this creates the couchable fields on the login pop up
         */
        nameEditText = (EditText) view.findViewById(R.id.name_edit);
        setCancelable(false);
        submitBtn = (Button) view.findViewById(R.id.submit_button);
        anim = AnimationUtils.loadAnimation(getContext(), R.anim.shake);

        /**
         *
         * @return creates our database accessor
         */
        mDbHelper = new LogDbHelper(getContext());


        /**
         *
         * @return creates a clickable button and calls the insert name method
         */
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emptyNameEditText = nameEditText.getText().toString().trim();
                String emptyName = "Please Enter Your Name!";
                if (emptyNameEditText.matches("")) {
                    nameEditText.setError(emptyName);
                    nameEditText.startAnimation(anim);
                } else {
                    insertName();
                    onDestroy();
                    dismiss();
                }
            }
        });
        return view;

    }
    /**
     *
     * @return inserts name into the database
     */
    private void insertName(){
        String nameString = nameEditText.getText().toString().trim();
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(LogDbEntry.COLUMN_INVENTORY_NAME, nameString);

        Long newRowId = db.insert(LogDbEntry.TABLE_NAME, null, value);
        if (newRowId == -1) {
            Toast.makeText(getContext(), "error saving name", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getContext(), "Welcome "+ nameString , Toast.LENGTH_LONG).show();
        }
    }


    /**
     * @return this creates items to be loaded into our cursor for display on the main screen
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch (i) {
            case URL_LOADER:
                // Returns a new CursorLoader
                return new CursorLoader(
                        getContext(),   // Parent activity context
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

    /**
     *
     * @return this implants new cursor into the display adapter
     */

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    /**
     *
     * @return Just a placeholder but loaderManager requires this be in this be implemented
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);

    }
}

