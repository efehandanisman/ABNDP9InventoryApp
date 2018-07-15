package com.example.android.abndp9inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;

/**
 * Created by Efehan on 22.6.2018.
 */

public class InventoryContract {

    private InventoryContract() {

    }

    public static final String CONTENT_AUTHORITY = "com.example.android.abndp9inventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+ CONTENT_AUTHORITY);
    public static final String PATH_INVENTORY = "inventory";

    public static final class InventoryEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

        public final static String _ID = BaseColumns._ID;
        public final static String TABLE_NAME = "products";
        public final static String COLUMN_PRODUCT_NAME = "name";
        public final static String COLUMN_PRODUCT_TYPE = "type";
        public final static String COLUMN_PRICE = "price";
        public final static String COLUMN_STOCK = "stock";
        public final static String COLUMN_DISCOUNT = "discount";
        public final static String COLUMN_QUANTITY = "quantity";
        public final static String COLUMN_SUPPLIER_PHONE = "phone";

        public final static int COLUMN_IN_STOCK = 1;
        public final static int COLUMN_OUTOF_STOCK = 0;
        public final static int COLUMN_IN_DISCOUNT = 1;
        public final static int COLUMN_NO_DISCOUNT = 0;

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"+CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

public static boolean StockStatus(int stock) {
    if( stock == COLUMN_IN_STOCK || stock == COLUMN_OUTOF_STOCK) {
            return true;
    }
    return false;


}
        public static boolean DiscountStatus(int discount) {
            if( discount == COLUMN_IN_DISCOUNT || discount == COLUMN_NO_DISCOUNT) {
                return true;
            }
            return false;
    }
}}
