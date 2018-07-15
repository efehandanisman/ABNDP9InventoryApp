package com.example.android.abndp9inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.abndp9inventoryapp.data.InventoryContract.InventoryEntry;

/**
 * Created by Efehan on 22.6.2018.
 */

public class InventoryDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "inventory.db";

    public InventoryDbHelper(Context context) { super(context, DATABASE_NAME,null,DATABASE_VERSION); }
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_ENTRIES = "CREATE TABLE" + InventoryEntry.TABLE_NAME + " ("
                + InventoryEntry._ID + " INTEGER PRIMARY KEY AUTO INCREMENT, "
                + InventoryEntry.COLUMN_PRODUCT_NAME + "TEXT NOT NULL, "
                + InventoryEntry.COLUMN_PRODUCT_TYPE + "TEXT NOT NULL, "
                + InventoryEntry.COLUMN_PRICE + "INTEGER NOT NULL,"
                + InventoryEntry.COLUMN_STOCK + "INTEGER NOT NULL,"
                + InventoryEntry.COLUMN_QUANTITY + "INTEGER NOT NULL, "
                + InventoryEntry.COLUMN_DISCOUNT + "INTEGER NOT NULL);"
                +InventoryEntry.COLUMN_SUPPLIER_PHONE + "INTEGER";

        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
