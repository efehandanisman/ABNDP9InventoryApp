package com.example.android.abndp9inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import static com.example.android.abndp9inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_DISCOUNT;
import static com.example.android.abndp9inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_IN_DISCOUNT;
import static com.example.android.abndp9inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_PRICE;
import static com.example.android.abndp9inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME;
import static com.example.android.abndp9inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_QUANTITY;
import static com.example.android.abndp9inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_STOCK;
import static com.example.android.abndp9inventoryapp.data.InventoryContract.InventoryEntry.COLUMN_SUPPLIER_PHONE;
import static com.example.android.abndp9inventoryapp.data.InventoryContract.InventoryEntry.DiscountStatus;
import static com.example.android.abndp9inventoryapp.data.InventoryContract.InventoryEntry.StockStatus;
import static com.example.android.abndp9inventoryapp.data.InventoryContract.InventoryEntry.TABLE_NAME;
import static com.example.android.abndp9inventoryapp.data.InventoryContract.InventoryEntry._ID;

/**
 * Created by Efehan on 22.6.2018.
 */

public class InventoryProvider extends ContentProvider {
    private static final int INVENTORY = 100;
    private static final int INVENTORY_ID = 101;
    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY,InventoryContract.PATH_INVENTORY,INVENTORY);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY,InventoryContract.PATH_INVENTORY + "/#",INVENTORY_ID);

    }

    private InventoryDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDbHelper(getContext());
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch(match) {
            case INVENTORY:
                cursor = database.query(TABLE_NAME,projection, selection, selectionArgs,null,null,sortOrder);
                break;
            case INVENTORY_ID:
                selection = _ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(TABLE_NAME, projection,selection,selectionArgs,null,null,sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown uri" + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch(match) {
            case INVENTORY:
                return saveProduct(uri,contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri saveProduct(Uri uri, ContentValues values) {
        String name = values.getAsString(COLUMN_PRODUCT_NAME);
        if(name== null) {
            throw new IllegalArgumentException("Product need a name");
        }
       Integer discount = values.getAsInteger(COLUMN_DISCOUNT);
        if(!DiscountStatus(discount)) {
        throw new IllegalArgumentException("You need to specify whether this product is in discount");
        }



        Integer Prodstock = values.getAsInteger(COLUMN_STOCK);
        if(!StockStatus(Prodstock)) {
            throw new IllegalArgumentException("You need to let us know whether we have this product or not");

        }

        Integer Prodquantity = values.getAsInteger(COLUMN_QUANTITY);
        if(Prodquantity < 0 || Prodquantity == null) {
            throw new IllegalArgumentException("You can not have negative # of products");
        }

        Integer Prodprice = values.getAsInteger(COLUMN_PRICE);
        if(Prodprice <= 0 || Prodprice == null) {
            throw new IllegalArgumentException("Someone needs to pay the bills, you can not give away products for free");
        }

Integer Suppphone = values.getAsInteger(COLUMN_SUPPLIER_PHONE);
        if(Suppphone == null) {
            throw new IllegalArgumentException("Who you gonna call?");

        }


        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri, id);

    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return updateInventory(uri, contentValues, selection, selectionArgs);
            case INVENTORY_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = _ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateInventory(uri, contentValues, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }

    }
    private int updateInventory(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
            if (values.containsKey(COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }

        if (values.containsKey(COLUMN_PRICE)) {
            Integer Prodprice = values.getAsInteger(COLUMN_PRICE);
            if (Prodprice <= 0 || Prodprice == null) {
                throw new IllegalArgumentException("Product requires a valid price");
            }
        }

        if (values.containsKey(COLUMN_SUPPLIER_PHONE)) {
            Integer Suppphone = values.getAsInteger(COLUMN_SUPPLIER_PHONE);
            if (Suppphone == null) {
                throw new IllegalArgumentException("Who you gonna call?");
            }
        }

        if (values.containsKey(COLUMN_QUANTITY)) {
            Integer Prodquantity = values.getAsInteger(COLUMN_QUANTITY);
            if (Prodquantity <= 0 || Prodquantity == null) {
                throw new IllegalArgumentException("Product requires a valid quantity");
            }
        }
        Integer discount = values.getAsInteger(COLUMN_DISCOUNT);
        if(!DiscountStatus(discount)) {
            throw new IllegalArgumentException("You need to specify whether this product is in discount");

        }

        Integer stock = values.getAsInteger(COLUMN_STOCK);
        if(!StockStatus(stock)) {
            throw new IllegalArgumentException("You need to let us know whether we have this product or not");

        }
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(InventoryContract.InventoryEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;

    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return InventoryContract.InventoryEntry.CONTENT_LIST_TYPE;
            case INVENTORY_ID:
                return InventoryContract.InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                rowsDeleted = database.delete(InventoryContract.InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case INVENTORY_ID:
                // Delete a single row given by the ID in the URI
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(InventoryContract.InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }


}

