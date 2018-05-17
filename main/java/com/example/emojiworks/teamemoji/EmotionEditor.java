package com.example.emojiworks.teamemoji;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.emojiworks.teamemoji.data.LogDbHelper;
import com.example.emojiworks.teamemoji.data.LogDbContract.LogDbEntry;



public class EmotionEditor extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private Uri mCurrentItemUri;
    private EditText mActivityEditText;
    private EditText mWhereEditText;
    private EditText mWhomEditText;
    private EditText mDescriptionEditText;
    private TextView nameText;
    private TextView sellTitle;
    private android.view.animation.Animation anim;
    LogDbHelper mDpHelper;
    EmotionCursorAdapter mCursorAdapter;

    private boolean mProductHasChanged = false;
    private static final int EXISTING_ENTRY_LOADER = 0;
    public static final String LOG_TAG = MainActivity.class.getName();

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        anim = AnimationUtils.loadAnimation(this, R.anim.shake);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new item or editing an existing one.
        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();

        /**
         *
         * @return This decides whether an item was clicked or a new item is being created to show
         * the user what they are doing in the top left of the screen.
         */
        if (mCurrentItemUri == null) {
            // This is a new item, so change the app bar to say "Add a Item"
            setTitle("Add Entry");
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing item, so change app bar to say "Edit Item"
            setTitle("Edit Entry");

            // Initialize a loader to read the  data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_ENTRY_LOADER, null, this);
        }

        // Find all relevant xml views being linked to our text edit boxes
        mActivityEditText = (EditText) findViewById(R.id.edit_activity);
        mWhereEditText = (EditText) findViewById(R.id.edit_where);
        mWhomEditText = (EditText) findViewById(R.id.edit_whom);
        mDescriptionEditText = (EditText) findViewById(R.id.edit_description);
        nameText = (TextView) findViewById((R.id.nameTextView));


        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mActivityEditText.setOnTouchListener(mTouchListener);
        mDescriptionEditText.setOnTouchListener(mTouchListener);
        mWhereEditText.setOnTouchListener(mTouchListener);
        mWhomEditText.setOnTouchListener(mTouchListener);


    }

    /**
     * @return This method connects our edit texts, takes the data and then moves it into a database
     * input format. Then it executes the insertion.
     */
    private void saveProduct() {
        String activityString = mActivityEditText.getText().toString().trim();
        String whomString = mWhomEditText.getText().toString().trim();
        String whereString = mWhereEditText.getText().toString().trim();
        String descriptionString = mDescriptionEditText.getText().toString().trim();

        String[] project = {
                LogDbEntry._ID,
                LogDbEntry.COLUMN_INVENTORY_NAME};
        String name = null;
        mDpHelper = new LogDbHelper(getApplicationContext());
        SQLiteDatabase db = mDpHelper.getReadableDatabase();
        Cursor cursor = db.query(LogDbEntry.TABLE_NAME, project, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndex(LogDbEntry.COLUMN_INVENTORY_NAME));
            nameText.setText(name);

        }
        if (mCurrentItemUri == null &&
                TextUtils.isEmpty(activityString) && TextUtils.isEmpty(descriptionString) &&
                TextUtils.isEmpty(whomString) && TextUtils.isEmpty(whereString)) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(LogDbEntry.COLUMN_INVENTORY_NAME, name);
        values.put(LogDbEntry.COLUMN_INVENTORY_ACTIVITY, activityString);
        values.put(LogDbEntry.COLUMN_INVENTORY_WHOM, whomString);
        values.put(LogDbEntry.COLUMN_INVENTORY_WHERE, whereString);
        values.put(LogDbEntry.COLUMN_INVENTORY_DESCRIPTION, descriptionString);
//

        // Determine if this is a new or existing item by checking if mCurrentItemUri is null or not
        if (mCurrentItemUri == null) {
            // This is a NEW item, so insert a new item into the provider,
            // returning the content URI for the new item.
            Uri newUri = getContentResolver().insert(LogDbEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.product_insertion_toast_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.product_insertion_toast_success),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING item, so update the item with content URI: mCurrentItemUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentItemUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.update_product_fail),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.update_product_success),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a Brand new item, hide the "Delete" menu item.
        if (mCurrentItemUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    /**
     * @return This switch statement helps us determine if all the edittext fields have data
     * entered into them
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save item to database
                String emptyActivityET = mActivityEditText.getText().toString().trim();
                String emptyDescET = mDescriptionEditText.getText().toString().trim();
                String emptyWhereET = mWhereEditText.getText().toString().trim();
                String emptyWhomET = mWhomEditText.getText().toString().trim();

                //Alerts user that a field is empty
                String emptyActivity = "Please Enter What You Are Doing!";
                String emptyDescription = "Please Enter The Details!";
                String emptyWhom = "Please Enter Who You Are With!";
                String emptyWhere = "Please Enter Where You're at!";

                // displays shake and alert for the user to enter data
                if (emptyActivityET.matches("")) {
                    mActivityEditText.setError(emptyActivity);
                    mActivityEditText.startAnimation(anim);
                } else if (emptyDescET.matches("")) {
                    mDescriptionEditText.setError(emptyDescription);
                    mDescriptionEditText.startAnimation(anim);
                } else if (emptyWhereET.matches("")) {
                    mWhereEditText.setError(emptyWhere);
                    mWhereEditText.startAnimation(anim);
                } else if (emptyWhomET.matches("")) {
                    mWhomEditText.setError(emptyWhom);
                    mWhomEditText.startAnimation(anim);

                } else {
                    saveProduct();
                    finish();
                }
                // Exit activity
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                // If the item hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EmotionEditor.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EmotionEditor.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * @return this creates items to be loaded into our cursor for display on the main screen
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                LogDbEntry._ID,
                LogDbEntry.COLUMN_INVENTORY_NAME,
                LogDbEntry.COLUMN_INVENTORY_ACTIVITY,
                LogDbEntry.COLUMN_INVENTORY_WHERE,
                LogDbEntry.COLUMN_INVENTORY_WHOM,
                LogDbEntry.COLUMN_INVENTORY_DESCRIPTION};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentItemUri,         // Query the content URI for the current item
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    /**
     * @return this actually takes the content in our cursor row and displays it in our edit
     * texts
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex(LogDbEntry.COLUMN_INVENTORY_NAME));
            int activityIndex = cursor.getColumnIndex(LogDbEntry.COLUMN_INVENTORY_ACTIVITY);
            int whereIndex = cursor.getColumnIndex(LogDbEntry.COLUMN_INVENTORY_WHERE);
            int whomIndex = cursor.getColumnIndex(LogDbEntry.COLUMN_INVENTORY_WHOM);
            int descriptionIndex = cursor.getColumnIndex(LogDbEntry.COLUMN_INVENTORY_DESCRIPTION);
            // Extract out the value from the Cursor for the given column index
//            String name = cursor.getString(nameColumnIndex);
            String activity = cursor.getString(activityIndex);
            String where = cursor.getString(whereIndex);
            String whom = cursor.getString(whomIndex);
            String description = cursor.getString(descriptionIndex);

            // Update the views on the screen with the values from the database
            nameText.setText(name);
            mActivityEditText.setText(activity);
            mWhereEditText.setText(where);
            mWhomEditText.setText(whom);
            mDescriptionEditText.setText(description);
        }
    }

    /**
     * @return For when our loader has invalid data, it clears all our input fields
     */

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mActivityEditText.setText("");
        mDescriptionEditText.setText("");
        mWhomEditText.setText("");
        mWhereEditText.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard_button, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * @return Asks the user if its ok to delete the current item
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_item_dialog);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the item.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel_deletion, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the item.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * @return takes the row id and deletes it from the database
     */
    private void deleteProduct() {
        if (mCurrentItemUri != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);

            //Show toast message depending on wether or not the delete was successful
            if (rowsDeleted == 0) {
                //If no rows deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.error_during_deletion),
                        Toast.LENGTH_SHORT).show();
            } else {
                //otherwise, the delete was successful and we can display a toast
                Toast.makeText(this, getString(R.string.delete_success),
                        Toast.LENGTH_SHORT).show();
            }
        }
        //Close the activity
        finish();
    }
}
