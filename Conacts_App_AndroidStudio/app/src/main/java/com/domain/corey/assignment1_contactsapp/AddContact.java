package com.domain.corey.assignment1_contactsapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class AddContact extends AppCompatActivity {

    /** Private class attributes for input fields */
    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText numberInput;
    private ImageView contactPhoto;
    private Bitmap contactPhotoBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        /** Set the toolbar and the navigation back button */
        Toolbar toolbar = findViewById(R.id.addContactToolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_clear_contact);

        /** Retrieve inputs from the layout file */
        firstNameInput = findViewById(R.id.first_name_input);
        lastNameInput = findViewById(R.id.last_name_input);
        numberInput = findViewById(R.id.phone_input);
        contactPhoto = findViewById(R.id.contact_photo);

        /** Set the bitmap variable to what is currently the image */
        contactPhoto.setDrawingCacheEnabled(true);
        contactPhoto.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        contactPhoto.layout(0, 0, contactPhoto.getMeasuredWidth(), contactPhoto.getMeasuredHeight());
        contactPhoto.buildDrawingCache(true);
        contactPhotoBitmap = Bitmap.createBitmap(contactPhoto.getDrawingCache());

        /** Camera Button for setting avatar image which opens the camera when clicked */
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_contact_photo);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePhotoIntent, 1);
            }
        });
    }

    /** Handle the result of the camera */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            contactPhoto.setImageBitmap(bitmap);
            contactPhotoBitmap = bitmap;
        }
        catch (Exception e) {
            Toast.makeText(AddContact.this, "Photo cancelled", Toast.LENGTH_SHORT).show();
        }

    }

    /** Inflate the menu; this adds items to the action bar if it is present */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contact, menu);
        return true;
    }

    /** Handle menu item selection */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        Intent intent = new Intent(AddContact.this, MainActivity.class);

        switch (id) {
            /** Handle navigation cancel button */
            case android.R.id.home:
                Toast.makeText(AddContact.this, "No Contact Was Created", Toast.LENGTH_SHORT).show();
                finish();
                return true;

            case R.id.save_contact:
                /** Retrieve input */
                String firstName = firstNameInput.getText().toString();
                String lastName = lastNameInput.getText().toString();
                String number = numberInput.getText().toString();

                // Conversion and passing retrieved from Zaid Daghestani at: https://stackoverflow.com/questions/11010386/passing-android-bitmap-data-within-activity-using-intent-in-android
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                if (contactPhotoBitmap != null) {
                    contactPhotoBitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
                }
                /** Validate input and then add them to the intent extras */
                if (validateInput(firstName, lastName, number))
                {
                    intent.putExtra("FIRST_NAME", firstName);
                    intent.putExtra("LAST_NAME", lastName);
                    intent.putExtra("NUMBER", number);
                    if (contactPhotoBitmap != null) {
                        intent.putExtra("PHOTO_BYTE_ARRAY", bs.toByteArray());
                        contactPhotoBitmap.recycle();
                    }

                    startActivity(intent);
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /** Validation for the data inputted by user */
    public boolean validateInput(String firstName, String lastName, String number)
    {
        boolean result = true;

        /** First Name Validation */
        if (firstName.isEmpty() || firstName == null) {
            Toast.makeText(AddContact.this, "First Name is required!", Toast.LENGTH_SHORT).show();
            return false;
        }
        for (char c : firstName.toCharArray())
        {
            if (Character.isDigit(c)) {
                Toast.makeText(AddContact.this, "First name cannot contain numbers!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        /** Last Name Validation */
        if (lastName.isEmpty() || lastName == null) {
            Toast.makeText(AddContact.this, "Last Name is required!", Toast.LENGTH_SHORT).show();
            return false;
        }
        for (char c : lastName.toCharArray())
        {
            if (Character.isDigit(c)) {
                Toast.makeText(AddContact.this, "Last name cannot contain numbers!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        /** Number Validation */
        if (number.isEmpty() || number == null) {
            Toast.makeText(AddContact.this, "Contact number is required!", Toast.LENGTH_SHORT).show();
            return false;
        }
        /** Number Length Validation */
        if (number.length() < 10 || number.length() > 12) {
            Toast.makeText(AddContact.this, "Contact number must be between 10 and 12 characters long", Toast.LENGTH_SHORT).show();
            return false;
        }
        /** Number digit Validation */
        for (char c : number.toCharArray())
        {
            if (Character.isDigit(c) == false) {
                return false;
            }
        }

        return result;
    }

}
