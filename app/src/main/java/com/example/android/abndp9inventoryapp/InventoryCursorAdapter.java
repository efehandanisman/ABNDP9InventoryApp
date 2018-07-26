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
import android.widget.Toast;

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
        final TextView quantity = (TextView) view.findViewById(R.id.quantity);

        Button sellButton = (Button) view.findViewById(R.id.sellProduct);

        final int idColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME);
        int productColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_TYPE);
        final int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_QUANTITY);

        String productName = cursor.getString(nameColumnIndex);
        String productType = cursor.getString(productColumnIndex);
        final int productQuantity = Integer.parseInt(cursor.getString(quantityColumnIndex));
        name.setText(productName);
        product.setText(productType);
        quantity.setText("Quantity" + productQuantity);

        if (productQuantity > 0) {
            sellButton.setVisibility(View.VISIBLE);

        } else {
            sellButton.setVisibility(View.GONE);
        }
        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Another one sold!", Toast.LENGTH_SHORT).show();

                Uri quantityUri = ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, idColumnIndex);
                ContentValues values = new ContentValues();
                values.put(InventoryContract.InventoryEntry.COLUMN_QUANTITY, productQuantity - 1);
                context.getContentResolver().update(quantityUri, values, null, null);

            }
        });
    }
}