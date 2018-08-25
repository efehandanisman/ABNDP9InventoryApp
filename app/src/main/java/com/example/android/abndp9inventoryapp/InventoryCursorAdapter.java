package com.example.android.abndp9inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.abndp9inventoryapp.data.InventoryContract;

/**
 * Created by Efehan on 22.6.2018.
 */

public class InventoryCursorAdapter extends CursorAdapter {

    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView name = (TextView) view.findViewById(R.id.name);
        TextView product = (TextView) view.findViewById(R.id.product);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        Button sellButton = (Button) view.findViewById(R.id.sellProduct);

        final long columnIdIndex = cursor.getLong(cursor.getColumnIndex(InventoryContract.InventoryEntry._ID));
        int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME);
        final int productColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_TYPE);

        final String quantity = cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_QUANTITY));
        String price = cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRICE));


        String productName = cursor.getString(nameColumnIndex);
        String productType = cursor.getString(productColumnIndex);
        name.setText(productName);
        product.setText(productType);
        priceTextView.setText(price);
        quantityTextView.setText(quantity);


        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // when sale is pressed - the quantity get -1
                int quantityInt = Integer.parseInt(quantity) - 1;
                ContentValues values = new ContentValues();
                values.put(InventoryContract.InventoryEntry.COLUMN_QUANTITY, quantityInt);
                Uri updateUri = ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, columnIdIndex);
                context.getContentResolver().update(updateUri, values, null, null);

            }


        });

        if (Integer.parseInt(quantity) > 0) {
            sellButton.setVisibility(View.VISIBLE);

        } else {
            sellButton.setVisibility(View.GONE);
        }

    }


}
