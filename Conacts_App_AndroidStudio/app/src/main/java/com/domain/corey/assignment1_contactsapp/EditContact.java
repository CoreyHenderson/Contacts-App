package com.domain.corey.assignment1_contactsapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
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

public class EditContact extends AppCompatActivity {

    /** Private class attributes for input fields */
    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText numberInput;
    private ImageView photoInput;
    private Bitmap contactPhotoBitmap;
    private int contactPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        /** Set the toolbar and the navigation back button */
        Toolbar toolbar = findViewById(R.id.editContactToolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_clear_contact);

        /** Retrieve inputs from the layout file */
        firstNameInput = findViewById(R.id.first_name_input);
        lastNameInput = findViewById(R.id.last_name_input);
        numberInput = findViewById(R.id.phone_input);
        photoInput = findViewById(R.id.contact_photo);

        /** Set the inputs based off the selected contact */
        firstNameInput.setText(getIntent().getExtras().getString("CONTACT_FIRSTNAME"));
        lastNameInput.setText(getIntent().getExtras().getString("CONTACT_LASTNAME"));
        numberInput.setText(getIntent().getExtras().getString("CONTACT_NUMBER"));
        byte []byteArray = getIntent().getExtras().getByteArray("CONTACT_PHOTO");
        contactPhotoBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        photoInput.setImageBitmap(contactPhotoBitmap);
        contactPosition = getIntent().getExtras().getInt("POSITION");


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
            photoInput.setImageBitmap(bitmap);
            contactPhotoBitmap = bitmap;
        }
        catch (Exception e) {
            Toast.makeText(EditContact.this, "Photo cancelled", Toast.LENGTH_SHORT).show();
        }

    }

    /** Inflate the menu; this adds items to the action bar if it is present */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    /** Handle menu item selection */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        final Intent intent = new Intent(EditContact.this, MainActivity.class);

        switch (id) {
            /** Handle navigation cancel button */
            case android.R.id.home:
                Toast.makeText(EditContact.this, "Edit cancelled", Toast.LENGTH_SHORT).show();
                finish();
                return true;

            case R.id.save_edit_contact:
                /** Retrieve input */
                String firstName = firstNameInput.getText().toString();
                String lastName = lastNameInput.getText().toString();
                String number = numberInput.getText().toString();

                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                if (contactPhotoBitmap != null) {
                    contactPhotoBitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
                }
                /** Validate input and then add them to the intent extras */
                if (validateInput(firstName, lastName, number))
                {
                    intent.putExtra("CONTACT_FIRSTNAME", firstName);
                    intent.putExtra("CONTACT_LASTNAME", lastName);
                    intent.putExtra("CONTACT_NUMBER", number);
                    if (contactPhotoBitmap != null) {
                        intent.putExtra("CONTACT_PHOTO", bs.toByteArray());
                        contactPhotoBitmap.recycle();
                    }
                    intent.putExtra("CONTACT_POSITION", contactPosition);

                    startActivity(intent);
                }

                return true;

            case R.id.action_delete:

                /** Create a confirmation dialog to confirm whether the user wants to delete to contact */
                AlertDialog.Builder alert = new AlertDialog.Builder(EditContact.this);
                alert.setMessage("Contact will be deleted.");
                alert.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        intent.putExtra("DELETECONTACT", true);
                        intent.putExtra("CONTACT_POSITION", contactPosition);
                        startActivity(intent);
                    }
                });
                alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                alert.show();
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
            Toast.makeText(EditContact.this, "First name is required!", Toast.LENGTH_SHORT).show();
            return false;
        }
        for (char c : firstName.toCharArray())
        {
            if (Character.isDigit(c)) {
                Toast.makeText(EditContact.this, "First name cannot contain numbers!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        /** Last Name Validation */
        if (lastName.isEmpty() || lastName == null) {
            Toast.makeText(EditContact.this, "Last name is required!", Toast.LENGTH_SHORT).show();
            return false;
        }
        for (char c : lastName.toCharArray())
        {
            if (Character.isDigit(c)) {
                Toast.makeText(EditContact.this, "Last name cannot contain numbers!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        /** Number Validation */
        if (number.isEmpty() || number == null) {
            Toast.makeText(EditContact.this, "Contact number is required!", Toast.LENGTH_SHORT).show();
            return false;
        }
        /** Number Length Validation */
        if (number.length() < 10 || number.length() > 12) {
            Toast.makeText(EditContact.this, "Contact number must be between 10 and 12 characters long", Toast.LENGTH_SHORT).show();
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