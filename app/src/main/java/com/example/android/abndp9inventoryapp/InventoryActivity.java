package com.example.android.abndp9inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import com.example.android.abndp9inventoryapp.data.InventoryContract;
import com.example.android.abndp9inventoryapp.data.InventoryDbHelper;

import android.support.design.widget.FloatingActionButton;
import android.widget.TextView;
import android.widget.Toast;

public class InventoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //Loader
    private static final int PROD_LOADER = 0;

    //Adapter of the listview

    InventoryCursorAdapter mCursorAdapter;
    InventoryDbHelper mDbHelper;
    Button orderNowButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent fabIntent = new Intent(InventoryActivity.this, InventoryManagementActivity.class);
                startActivity(fabIntent);
            }
        });



        ListView productListView = (ListView) findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty_view);
        productListView.setEmptyView(emptyView);

        mCursorAdapter = new InventoryCursorAdapter(this, null);

        productListView.setAdapter(mCursorAdapter);
        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(InventoryActivity.this, InventoryManagementActivity.class);
                Uri currentProdUri = ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, id);
                intent.setData(currentProdUri);
                startActivity(intent);

            }
        });
        getLoaderManager().initLoader(PROD_LOADER, null, this);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    // Taken from https://github.com/Muneera-Salah/Inventory-App-Stage-2/blob/master/app/src/main/java/com/example/anrdoid/inventoryappstage2/InventoryActivity.java
    public void sale(int productID, String mQuantity) {
        Integer quantity = Integer.parseInt(mQuantity);
        quantity = quantity - 1;
        if (quantity > 0) {
            ContentValues values = new ContentValues();
            values.put(InventoryContract.InventoryEntry.COLUMN_QUANTITY, mQuantity);
            Uri updateUri = ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, productID);
            int rowsAffected = getContentResolver().update(updateUri, values, null, null);
            Toast.makeText(this, "Product sold", Toast.LENGTH_SHORT).show();

            Log.d("Log msg", "rowsAffected " + rowsAffected + " - productID " + productID + " - quantity " + mQuantity + " , decreaseCount has been called.");
        } else {
            Toast.makeText(this, "We don't have this product anymore", Toast.LENGTH_SHORT).show();
        }
    }



    private void saveProduct() {

        // Create a ContentValues object where column names are the keys,
        // and attributes are the values.
        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME, "name");
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_TYPE, "type");
        values.put(InventoryContract.InventoryEntry.COLUMN_PRICE, "price");
        values.put(InventoryContract.InventoryEntry.COLUMN_DISCOUNT, "discount");
        values.put(InventoryContract.InventoryEntry.COLUMN_STOCK, "stock");
        values.put(InventoryContract.InventoryEntry.COLUMN_QUANTITY,"quantity");
        values.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_PHONE, "phone");
        Uri uri = getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_inventoryactivity, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.action_save:
                saveProduct();
                finish();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAll();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_TYPE,
                InventoryContract.InventoryEntry.COLUMN_PRICE,
                InventoryContract.InventoryEntry.COLUMN_STOCK,
                InventoryContract.InventoryEntry.COLUMN_DISCOUNT,
                InventoryContract.InventoryEntry.COLUMN_QUANTITY,
                InventoryContract.InventoryEntry.COLUMN_SUPPLIER_PHONE,

        };

        return new CursorLoader(this, InventoryContract.InventoryEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    private void deleteAll() {
        int rowsDeleted = getContentResolver().delete(InventoryContract.InventoryEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from database");
    }



}