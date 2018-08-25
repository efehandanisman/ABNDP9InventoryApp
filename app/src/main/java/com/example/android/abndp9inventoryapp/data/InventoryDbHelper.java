package com.example.android.abndp9inventoryapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.abndp9inventoryapp.data.InventoryContract.InventoryEntry;

/**
 * Created by Efehan on 22.6.2018.
 */

public class InventoryDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "inventory.db";

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_ENTRIES = "CREATE TABLE " + InventoryEntry.TABLE_NAME + " ("
                + InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + InventoryEntry.COLUMN_PRODUCT_TYPE + "    TEXT NOT NULL, "
                + InventoryEntry.COLUMN_PRICE + " TEXT NOT NULL DEFAULT 0, "
                + InventoryEntry.COLUMN_STOCK + " INTEGER NOT NULL, "
                + InventoryEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + InventoryEntry.COLUMN_DISCOUNT + " INTEGER NOT NULL, "
                + InventoryEntry.COLUMN_SUPPLIER_PHONE + " TEXT NOT NULL);";

        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public Cursor readStock() {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_TYPE,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryContract.InventoryEntry.COLUMN_PRICE,
                InventoryContract.InventoryEntry.COLUMN_QUANTITY,
                InventoryEntry.COLUMN_STOCK,
                InventoryEntry.COLUMN_DISCOUNT,
                InventoryContract.InventoryEntry.COLUMN_SUPPLIER_PHONE,
        };
        Cursor cursor = db.query(
                InventoryEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        return cursor;
    }

    public void sellOneItem(long itemId, int quantity) {
        SQLiteDatabase db = getWritableDatabase();
        int newQuantity = 0;
        if (quantity > 0) {
            newQuantity = quantity - 1;
        }
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_QUANTITY, newQuantity);
        String selection = InventoryEntry._ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(itemId)};
        db.update(InventoryEntry.TABLE_NAME,
                values, selection, selectionArgs);
    }

}
