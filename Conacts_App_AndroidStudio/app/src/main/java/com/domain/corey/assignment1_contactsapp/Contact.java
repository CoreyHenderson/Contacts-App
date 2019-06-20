package com.domain.corey.assignment1_contactsapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import java.util.Comparator;

public class Contact {

    /** Local Variables */
    private String firstName;
    private String lastName;
    private String Number;
    private ImageView Image;

    /** Custom Constructor with a bitmap for the ImageView (if the user provides a photo) */
    public Contact(String fname, String lname, String number, Bitmap bitmapImage, Context context)
    {
        firstName = fname;
        lastName = lname;
        Number = number;
        Image = new ImageView(context);
        Image.setImageBitmap(bitmapImage);
    }

    /** Custom Constructor without photo */
    public Contact(String fname, String lname, String number, Context context)
    {
        firstName = fname;
        lastName = lname;
        Number = number;
        Image = new ImageView(context);
        Image.setImageResource(R.drawable.ic_person_black);
    }

    /** Public Get Methods */
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getNumber() {
        return Number;
    }
    public ImageView getImage() {
        return Image;
    }
    /** Creates a Bitmap of the image using the Drawing Cache */
    public Bitmap getImageBitmap() {
        Image.setDrawingCacheEnabled(true);
        Image.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        Image.layout(0, 0, Image.getMeasuredWidth(), Image.getMeasuredHeight());
        Image.buildDrawingCache(true);
        int ImageHeight = Image.getMeasuredHeight();
        int ImageWidth = Image.getMeasuredWidth();
        Bitmap bitmap = Bitmap.createBitmap(Image.getDrawingCache());
        return bitmap;
    }           // Method retrieved from winklerrr: https://stackoverflow.com/questions/8306623/get-bitmap-attached-to-imageview

    /** Public Set Methods */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setNumber(String number) {
        Number = number;
    }
    public void setImage(ImageView image) {
        Image = image;
    }
    public void setImageBitmap(Bitmap bitmap) {
        Image.setImageBitmap(bitmap);
    }


    /** Comparator for sorting the list by contact name */
    public static Comparator<Contact> contactNameComparator = new Comparator<Contact>() {
        @Override
        public int compare(Contact contact, Contact t1) {
            String contactName1 = contact.getFirstName().toLowerCase();
            String contactName2 = t1.getFirstName().toLowerCase();

            return contactName1.compareTo(contactName2);
        }
    };
}
