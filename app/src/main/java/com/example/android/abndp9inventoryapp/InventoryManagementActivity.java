package com.example.android.abndp9inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.abndp9inventoryapp.data.InventoryContract;
import com.example.android.abndp9inventoryapp.data.InventoryContract.InventoryEntry;

/**
 * Created by Efehan on 22.6.2018.
 */

public class InventoryManagementActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private final static int EXISTING_PROD_LOADER = 0;
    private Uri mCurrentProdUri;
    private boolean mProdHasChanged = false;
    private EditText mNameEditText;
    private EditText mType;
    private EditText mPrice;
    private Spinner mStockSpinner;
    private Spinner mDiscountSpinner;
    private TextView mQuantityView;
    private int mQuantity;
    private EditText mSupplierPhoneNumber;
    Button sellProduct;
    Button addProduct;
    String spinnerName;

    private int mStock = InventoryContract.InventoryEntry.COLUMN_IN_STOCK;
    private int mDiscount = InventoryContract.InventoryEntry.COLUMN_NO_DISCOUNT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventorymanagement);

        if(mCurrentProdUri == null) {
            setTitle("Add a product");
            invalidateOptionsMenu();
        }else {
            setTitle("Edit Product");
            Intent intent = getIntent();
            mCurrentProdUri = intent.getData();
            getLoaderManager().initLoader(EXISTING_PROD_LOADER,null,this);

        }

        mNameEditText = (EditText) findViewById(R.id.edit_prod_name);
        mType = (EditText) findViewById(R.id.edit_type);
        mPrice = (EditText) findViewById(R.id.price);
        mStockSpinner = (Spinner) findViewById(R.id.spinner_stock);
        mDiscountSpinner = (Spinner) findViewById(R.id.spinner_discount);
        mQuantityView= (TextView) findViewById(R.id.quantity);
        mSupplierPhoneNumber = (EditText) findViewById(R.id.phone);

        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(13);

        mSupplierPhoneNumber.setFilters(filters);

        setupSpinner();
        mNameEditText.setOnTouchListener(mTouchListener);
        mType.setOnTouchListener(mTouchListener);
        mPrice.setOnTouchListener(mTouchListener);
        mStockSpinner.setOnTouchListener(mTouchListener);
        mDiscountSpinner.setOnTouchListener(mTouchListener);
        mSupplierPhoneNumber.setOnTouchListener(mTouchListener);

final Button incrementButton = (Button) findViewById(R.id.increase_button);
        incrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // On click quantity increases
                mQuantity++;
                // display quantity
                displayQuantity();
            }
        });


        final Button decrementButton = (Button) findViewById(R.id.decrease_button);
        decrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on click quantity decreases
                decrementButton(v);
                // display quantity
                displayQuantity();
            }
        });

        final Button orderNowButton = (Button) findViewById(R.id.order_button);
        orderNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifier()) {
                    // Check that fields are not empty
                    orderNow();
                }
            }
        });}




    public void decrementButton(View view) {
        // If quantity = 0 show toast message
        if (mQuantity == 0) {
            Toast.makeText(this, "You can't have less than 0 product", Toast.LENGTH_SHORT).show();
        } else {
            // If quantity is not 0, decrease quantity
            mQuantity--;
            displayQuantity();
        }
    }

    public void displayQuantity() {
        TextView quantityView = (TextView) findViewById(R.id.quantity);
        quantityView.setText(String.valueOf(mQuantity));
    }

    public void orderNow() {
        // If quantity id 0, display toast message
        if (mQuantity == 0) {
            Toast.makeText(this, "Quantity is required", Toast.LENGTH_SHORT).show();

        } else {
            // Else display toast message
            Toast.makeText(this, "Products have been successfully ordered", Toast.LENGTH_SHORT).show();

            // Set an intent that makes the user go to the Phone Call
            // when the order is done.
            String phoneNumber = mSupplierPhoneNumber.getText().toString().trim();
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            if (callIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(callIntent);
            }
        }
    }


    private boolean verifier() {

        String nameString = mNameEditText.getText().toString().trim();
        String typeString = mType.getText().toString().trim();
        String priceString = mPrice.getText().toString().trim();
        String supplierString = mSupplierPhoneNumber.getText().toString().trim();
        String stockInt = mStockSpinner.getSelectedItem().toString().trim();
        String discountInt = mDiscountSpinner.getSelectedItem().toString().trim();


        if(mStockSpinner != null && mStockSpinner.getSelectedItem() !=null ) {
            stockInt = (String)mStockSpinner.getSelectedItem();
        } else  {

        }

        if(mDiscountSpinner != null && mDiscountSpinner.getSelectedItem() !=null ) {
            discountInt = (String)mDiscountSpinner.getSelectedItem();
        } else  {

        }

        // Check that all fields in the EditText view are completed
        // No need to check for negative values of price and quantity because
        // only positive inputs are possible as specified in activity_editor.xml inputType (is NOT signed)
        if (TextUtils.isEmpty(nameString) ||
                TextUtils.isEmpty(typeString) ||
                TextUtils.isEmpty(priceString) ||
                TextUtils.isEmpty(supplierString) ||
                TextUtils.isEmpty(stockInt) ||
                TextUtils.isEmpty(discountInt)) {
            Toast.makeText(this, "You did not fill all the fields", Toast.LENGTH_SHORT).show();
            return false;
        } else if (Double.parseDouble(priceString) == 0) {
            Toast.makeText(this, "Price can't be 0", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }


    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter stockSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_stock_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        stockSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mStockSpinner.setAdapter(stockSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mStockSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.in_stock))) {
                        mStock = InventoryContract.InventoryEntry.COLUMN_IN_STOCK; // IN STOCK
                    } else {
                        mStock = InventoryContract.InventoryEntry.COLUMN_OUTOF_STOCK; // OUT OF STOCK
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mStock = 0; // Unknown
            }
        });

}

private void saveProduct() {
String nameString = mNameEditText.getText().toString().trim();
    String typeString = mType.getText().toString().trim();
    String priceString = mPrice.getText().toString().trim();
    String quantityString = mQuantityView.getText().toString().trim();
    String phoneString = mSupplierPhoneNumber.getText().toString().trim();

    if (mCurrentProdUri == null &&
            TextUtils.isEmpty(nameString) && TextUtils.isEmpty(typeString) &&
            TextUtils.isEmpty(priceString) && TextUtils.isEmpty(phoneString)) {
        finish();
        return;
    }
    ContentValues values = new ContentValues();
values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME,nameString);
values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_TYPE,typeString);
values.put(InventoryEntry.COLUMN_PRICE,priceString);
    values.put(InventoryEntry.COLUMN_QUANTITY,quantityString);
    values.put(InventoryEntry.COLUMN_SUPPLIER_PHONE,phoneString);
    values.put(InventoryEntry.COLUMN_DISCOUNT,mDiscount);
    values.put(InventoryEntry.COLUMN_STOCK,mStock);


if (mCurrentProdUri == null) {

    Uri newUri = getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI,values);
    if (newUri == null) {
        Toast.makeText(this, "Product can not be saved", Toast.LENGTH_SHORT).show();
    }else{
        Toast.makeText(this,"Product updated",Toast.LENGTH_SHORT).show();

    }
} else {
    int rowsAffected = getContentResolver().update(mCurrentProdUri, values, null, null);
    if (rowsAffected == 0) {
        // If no rows were affected, then there was an error with the update.
        Toast.makeText(this, getString(R.string.update_prod_failed),
                Toast.LENGTH_SHORT).show();

    } else {
        // Otherwise, the update was successful and we can display a toast.
        Toast.makeText(this, getString(R.string.update_prod_succeed),
                Toast.LENGTH_SHORT).show();
    }
}

}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_managementactivity, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                if(verifier()) {

                    saveProduct();
                    finish();
                }
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
            return true;
            case android.R.id.home:
                if(!mProdHasChanged) {
                    NavUtils.navigateUpFromSameTask(InventoryManagementActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(InventoryManagementActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
String[] projection = {
        InventoryEntry._ID,
        InventoryEntry.COLUMN_PRODUCT_NAME,
        InventoryEntry.COLUMN_PRODUCT_TYPE,
        InventoryEntry.COLUMN_STOCK,
        InventoryEntry.COLUMN_PRICE,
        InventoryEntry.COLUMN_QUANTITY,
        InventoryEntry.COLUMN_DISCOUNT,
        InventoryEntry.COLUMN_SUPPLIER_PHONE};
        return new CursorLoader(this,mCurrentProdUri,projection,null,null,null);
}

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {

        }
        // Find the columns of attributes that we're interested in

        int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME);
        int productColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_TYPE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_QUANTITY);
        int stockColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_STOCK);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE);
        int supplierphoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_PHONE);
        int discountColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_DISCOUNT);

        // Extract out the value from the Cursor for the given column index
String name = cursor.getString(nameColumnIndex);
String product = cursor.getString(productColumnIndex);
int quantity = cursor.getInt(quantityColumnIndex);
int stock = cursor.getInt(stockColumnIndex);
int discount = cursor.getInt(discountColumnIndex);
String phone = cursor.getString(supplierphoneColumnIndex);
int price = cursor.getInt(priceColumnIndex);

mNameEditText.setText(name);
mType.setText(product);
mSupplierPhoneNumber.setText(phone);
mQuantityView.setText(Integer.toString(quantity));
mPrice.setText(Integer.toString(price));

switch(stock) {
    case InventoryEntry.COLUMN_IN_STOCK:
        mStockSpinner.setSelection(1);
        break;
    case InventoryEntry.COLUMN_OUTOF_STOCK:
        mStockSpinner.setSelection(2);
        break;
    default:
        mStockSpinner.setSelection(1);
        break;
}

switch(discount) {
    case InventoryEntry.COLUMN_IN_DISCOUNT:
        mDiscountSpinner.setSelection(1);
        break;
    case InventoryEntry.COLUMN_NO_DISCOUNT:
        mDiscountSpinner.setSelection(2);
        break;
    default:
        mDiscountSpinner.setSelection(2);
        break;
}
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
mNameEditText.setText("");
mSupplierPhoneNumber.setText("");
mType.setText("");
mPrice.setText("");
mQuantityView.setText("");
        mDiscountSpinner.setSelection(2);
        mStockSpinner.setSelection(1);
        }

        private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
            mProdHasChanged = true;
            return false;
        }
    };
private void showUnsavedChangesDialog(
        DialogInterface.OnClickListener discardButtonClickListener) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage("Do you want to delete all changes and return to main page");
    builder.setPositiveButton("Delete and Return",discardButtonClickListener);
    builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int id) {
            if(dialog != null) {
                dialog.dismiss();
            }
        }
    });
    AlertDialog alertDialog = builder.create();
    alertDialog.show();
}


    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mProdHasChanged) {
            super.onBackPressed();
            return;
        }
        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentProdUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        android.support.v7.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }



    /**
     * Perform the deletion of the product in the database.
     */
    private void deletePet() {
        if (mCurrentProdUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentProdUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_prod_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_prod_successful),
                        Toast.LENGTH_SHORT).show();
                finish();

            }


        }
    }
}