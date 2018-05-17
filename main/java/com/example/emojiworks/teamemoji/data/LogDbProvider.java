package com.example.emojiworks.teamemoji.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.emojiworks.teamemoji.data.LogDbHelper;

import static com.example.emojiworks.teamemoji.data.LogDbContract.LogDbEntry.CONTENT_AUTHORITY;
import static com.example.emojiworks.teamemoji.data.LogDbContract.LogDbEntry;


public class LogDbProvider extends ContentProvider {
    private static final int PRODUCT = 100;
    private static final int PRODUCT_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static{
        sUriMatcher.addURI(CONTENT_AUTHORITY, LogDbEntry.PATH_INVENTORY, PRODUCT);
        sUriMatcher.addURI(CONTENT_AUTHORITY, LogDbEntry.PATH_INVENTORY + "/#", PRODUCT_ID);

    }
    public static final String LOG_TAG = LogDbProvider.class.getSimpleName();
    private LogDbHelper mDbHelper;


    @Override
    public boolean onCreate() {
        mDbHelper = new LogDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                cursor = database.query(LogDbEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = LogDbEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(LogDbEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                return LogDbEntry.CONTENT_ITEM_TYPE;
            case PRODUCT_ID:
                return LogDbEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }
    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case PRODUCT:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }

    }
    private Uri insertProduct(Uri uri, ContentValues values) {

        String name = values.getAsString(LogDbEntry.COLUMN_INVENTORY_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Pet require a name");
        }

        //Check that the name is not null
        String activity = values.getAsString(LogDbEntry.COLUMN_INVENTORY_ACTIVITY);
        if (activity == null) {
            throw new IllegalArgumentException("Pet require a name");
        }
        String description = values.getAsString(LogDbEntry.COLUMN_INVENTORY_DESCRIPTION);
        if (description == null) {
            throw new IllegalArgumentException("Pet require a description");
        }

        // Check that the gender is valid
        String whom = values.getAsString(LogDbEntry.COLUMN_INVENTORY_WHOM);
        if (whom == null) {
            throw new IllegalArgumentException("Pet requires valid price");
        }

        //If weight is provided, check that it's greater than or equal to 0 kg
        String where = values.getAsString(LogDbEntry.COLUMN_INVENTORY_WHERE);
        if (where == null) {
            throw new IllegalArgumentException("Pet requires valid stockStatus");}

        //get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //Insert the new pet with the given values
        long id = database.insert(LogDbEntry.TABLE_NAME, null, values);

        //If id = -1, then the insertion failed . Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        //Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match){
            case PRODUCT:
                rowsDeleted = database.delete(LogDbEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                selection = LogDbEntry._ID+"=?";
                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))
                };
                rowsDeleted = database.delete(LogDbEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted == 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCT_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = LogDbEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }
    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link PetEntry#COLUMN_PET_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(LogDbEntry.COLUMN_INVENTORY_ACTIVITY)) {
            String name = values.getAsString(LogDbEntry.COLUMN_INVENTORY_ACTIVITY);
            if (name == null) {
                throw new IllegalArgumentException("product requires a name");
            }
        }
        if (values.containsKey(LogDbEntry.COLUMN_INVENTORY_DESCRIPTION)) {
            String description = values.getAsString(LogDbEntry.COLUMN_INVENTORY_DESCRIPTION);
            if (description == null) {
                throw new IllegalArgumentException("product requires a description");
            }
        }

        // If the {@link PetEntry#COLUMN_PET_GENDER} key is present,
        // check that the gender value is valid.
        if (values.containsKey(LogDbEntry.COLUMN_INVENTORY_WHERE)) {
            String where = values.getAsString(LogDbEntry.COLUMN_INVENTORY_WHERE);
            if (where == null) {
                throw new IllegalArgumentException("field requires location");
            }
        }

        // If the {@link PetEntry#COLUMN_PET_WEIGHT} key is present,
        // check that the weight value is valid.
        if (values.containsKey(LogDbEntry.COLUMN_INVENTORY_WHOM)) {
            // Check that the weight is greater than or equal to 0 kg
            String whom = values.getAsString(LogDbEntry.COLUMN_INVENTORY_WHOM);
            if (whom == null) {
                throw new IllegalArgumentException("product requires a stock amount");
            }
        }

        // No need to check the breed, any value is valid (including null).

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(LogDbEntry.TABLE_NAME, values, selection, selectionArgs);
        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }
}
