package com.domain.corey.assignment1_contactsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

//////////////////////////////////////
/**   Main Activity Class           */
//////////////////////////////////////
public class MainActivity extends AppCompatActivity {

    /** Private Attributes */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    /** Public Attributes (For retrieving Intent data from 'AddContact' and 'EditContact' activity) */
    public static String addContactFirstName;
    public static String addContactLastName;
    public static String addContactNumber;
    public static byte[] addContactImageByteArray;
    public static String editContactFirstName;
    public static String editContactLastName;
    public static String editContactNumber;
    public static byte[] editContactImageByteArray;
    public static boolean canDeleteAll = false;
    public static int deleted = 0;
    public static boolean canDeleteContact = false;
    public static int contactPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** Set the toolbar */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /** Create the adapter that will return a fragment for each of the two
            primary sections of the activity. */
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        /** Set up the ViewPager with the sections adapter. */
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        /** Acquire the tabs within the toolbar */
        TabLayout tabLayout = findViewById(R.id.tabs);

        /** Set the different view sections for the tabs */
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        /** Set an icon for each tab */
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_contacts);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_star);

        /** Set the floating action button to start the 'Add contact' activity */
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddContact.class);
                startActivity(intent);
            }
        });

        /** Set the add contact variables for the class */
        if (getIntent().hasExtra("FIRST_NAME") || getIntent().hasExtra("LAST_NAME") || getIntent().hasExtra("NUMBER")) {
            addContactFirstName = getIntent().getExtras().getString("FIRST_NAME");
            addContactLastName = getIntent().getExtras().getString("LAST_NAME");
            addContactNumber = getIntent().getExtras().getString("NUMBER");

            if (getIntent().hasExtra("PHOTO_BYTE_ARRAY")) {
                addContactImageByteArray = getIntent().getByteArrayExtra("PHOTO_BYTE_ARRAY");
            }
        }

        /** Set the edit contact variables for the class */
        if (getIntent().hasExtra("CONTACT_FIRSTNAME") || getIntent().hasExtra("CONTACT_LASTNAME") || getIntent().hasExtra("CONTACT_NUMBER")) {
            editContactFirstName = getIntent().getExtras().getString("CONTACT_FIRSTNAME");
            editContactLastName = getIntent().getExtras().getString("CONTACT_LASTNAME");
            editContactNumber = getIntent().getExtras().getString("CONTACT_NUMBER");

            if (getIntent().hasExtra("CONTACT_PHOTO")) {
                editContactImageByteArray = getIntent().getByteArrayExtra("CONTACT_PHOTO");
            }
        }

        /** Get the contacts' position for when editing or deleting a contact */
        if (getIntent().hasExtra("CONTACT_POSITION")) {
            contactPosition = getIntent().getExtras().getInt("CONTACT_POSITION");
        }
        else
            contactPosition = -1;

        if (getIntent().hasExtra("DELETECONTACT")) {
            canDeleteContact = getIntent().getExtras().getBoolean("DELETECONTACT");
        }
    }

    /** Inflate the menu; this adds items to the action bar if it is present. */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        /** Find and set the search bar listener */
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView)item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mSectionsPagerAdapter.getInstance(mViewPager.getCurrentItem()).filter(s);
                return false;
            }
        });

        return true;
    }

    /** Action for when one of the toolbar options are selected */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.action_search:
                Toast.makeText(MainActivity.this, "Search Menu", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_block:
                Toast.makeText(MainActivity.this, "Block Contacts", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_importOrExport:
                Toast.makeText(MainActivity.this, "Import/Export Contacts", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_help:
                Toast.makeText(MainActivity.this, "Help Menu", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_deleteAll:
                canDeleteAll = true;
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        getIntent().removeExtra("FIRST_NAME");
        getIntent().removeExtra("LAST_NAME");
        getIntent().removeExtra("NUMBER");
        getIntent().removeExtra("PHOTO_BYTE_ARRAY");
        getIntent().removeExtra("CONTACT_FIRSTNAME");
        getIntent().removeExtra("CONTACT_LASTNAME");
        getIntent().removeExtra("CONTACT_NUMBER");
        getIntent().removeExtra("CONTACT_PHOTO");
        getIntent().removeExtra("CONTACT_POSITION");
        getIntent().removeExtra("CANDELETE");
    }

    /**
     *  Nested Fragment class containing the recycler view of contacts
     */
    public static class PlaceholderFragment extends Fragment {

        /** Private Attributes */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private RecyclerView myRecyclerView;
        private RecyclerViewAdapter recyclerAdapter;
        private List<Contact> contacts;
        View rootView;

        /** Public Attributes (for shared preferences) */
        public static final String PREFS_NAME = "SHARED_PREFS";
        public static final String COUNT_KEY = "COUNT";

        public PlaceholderFragment() {
        }

        /**
         *  Returns a new instance of this fragment for the given section number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            rootView = inflater.inflate(R.layout.fragment_main, container, false);

            /** Set the recycler view adapter */
            recyclerAdapter = new RecyclerViewAdapter(getContext(), contacts);
            myRecyclerView = rootView.findViewById(R.id.contact_recycler_view);
            myRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            myRecyclerView.setAdapter(recyclerAdapter);

            return rootView;
        }

        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);


            /** Create the list for the contacts */
            if (contacts == null) {
                contacts = new ArrayList<>();
            }

            /** Checks if the DeleteAllContacts button has been pressed and runs the clear preferences function */
            if (canDeleteAll == true) {
                ClearSharedPreference();
                contacts = new ArrayList<>();
                if (deleted == 1) {
                    canDeleteAll = false;
                    deleted = 0;
                }
                deleted++;
            }
            else if (contactPosition >= 0) {

                /** Loads the contacts from the Shared Preferences */
                LoadData();

                if (!canDeleteContact && editContactFirstName != null) {

                    Bitmap img = BitmapFactory.decodeByteArray(editContactImageByteArray, 0, editContactImageByteArray.length);
                    contacts.get(contactPosition).setFirstName(editContactFirstName);
                    contacts.get(contactPosition).setLastName(editContactLastName);
                    contacts.get(contactPosition).setNumber(editContactNumber);
                    contacts.get(contactPosition).setImageBitmap(img);
                }
                else if (canDeleteContact ) {
                    contacts.remove(contactPosition);
                }
            }
            else {
                /** Loads the contacts from the Shared Preferences */
                LoadData();

                boolean sameContact = false;
                /** Checks any matching/duplicate Contacts */
                if (contacts.size() > 0 && addContactFirstName != null) {
                    for (Contact contact : contacts) {
                        if (addContactFirstName.toLowerCase() == contact.getFirstName().toLowerCase() && addContactLastName.toLowerCase() == contact.getLastName().toLowerCase())
                            sameContact = true;
                        if (addContactNumber == contact.getNumber())
                            sameContact = true;
                    }
                }

                /** Check if the user has made a new contact and ensure they didn't press cancel add contact */
                if (addContactFirstName != null && sameContact == false) {

                    /** Check whether the user provided a picture and whether the contact hasn't already been made
                     *  Then add the contact to the list
                     */
                    if (savedInstanceState == null) {
                        if (addContactImageByteArray != null) {
                            Bitmap img = BitmapFactory.decodeByteArray(addContactImageByteArray, 0, addContactImageByteArray.length);
                            contacts.add(new Contact(addContactFirstName, addContactLastName, addContactNumber, img, getContext()));
                        }
                        else
                            contacts.add(new Contact(addContactFirstName, addContactLastName, addContactNumber, getContext()));
                    }
                }
            }

            /** Sort the list of contacts into alphabetical order */
            Collections.sort(contacts, Contact.contactNameComparator);          // Sorting method retrieved from: https://beginnersbook.com/2013/12/java-arraylist-of-object-sort-example-comparable-and-comparator/
        }

        @Override
        public void onSaveInstanceState(@NonNull Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putInt("VALUE", 1);
        }

        /** Filters the recyclerViewAdapter for the search functionality */
        public void filter(String text) {
            ArrayList<Contact> filteredList = new ArrayList<>();

            for (Contact item : contacts) {
                if (item.getFirstName().toLowerCase().contains(text.toLowerCase()) || item.getLastName().toLowerCase().contains(text.toLowerCase()) || item.getNumber().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item);
                }
            }

            recyclerAdapter.filterList(filteredList);
        }

        /** Save the all the contacts in the list to Shared Preferences every time the activity is paused */
        @Override
        public void onPause() {
            super.onPause();

            /** Remove the selected contact from shared preferences if they have deleted */
            if (canDeleteContact) {
                RemoveContact(contactPosition);
                canDeleteContact = false;
            }

            SaveData();
        }

        /**
         *  SaveData function for saving Shared Preferences
         */
        public void SaveData() {
            SharedPreferences settings = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();

            if (contacts.size() >= 1) {
                /** Include the size of the list so LoadData knows how many items to iterate through */
                editor.putInt(COUNT_KEY, contacts.size());

                /** Loop through all objects in the list */
                int count = 0;
                for (Contact c : contacts) {

                    /** Gather all string attributes from the current Contact */
                    String fname = c.getFirstName();
                    String lname = c.getLastName();
                    String number = c.getNumber();

                    /** Get the ImageView Bitmap and convert it into an array of bytes */
                    Bitmap photo = c.getImageBitmap();
                    ByteArrayOutputStream bs = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.PNG, 50, bs);
                    byte[] byteArray = bs.toByteArray();

                    /** Join all the attributes and the byte array together in one single string */
                    List<String> StringList = new ArrayList<String>();
                    StringList.add(fname);
                    StringList.add(lname);
                    StringList.add(number);
                    StringList.add(Base64.encodeToString(byteArray, Base64.DEFAULT));
                    String objectData = TextUtils.join(":", StringList);

                    /** Feed the string containing all the objects data into the Shared Preferences */
                    editor.putString(Integer.toString(count), objectData);
                    count++;
                }

                editor.commit();
            }

        }
        /**
         *  LoadData function for retrieving Shared Preferences
         */
        public void LoadData()
        {
            SharedPreferences settings = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

            if (settings.contains(COUNT_KEY)) {

                int objectCount = settings.getInt(COUNT_KEY, 0);

                /** Loop through every object that has a key in the Shared Preference */
                for (int i = 0; i < objectCount; i++) {

                    /** Get the associated string for one of the contacts */
                    String objectDataAsString = settings.getString(Integer.toString(i), "");
                    /** Split the string into its separate attributes */
                    List<String> objectData = Arrays.asList(objectDataAsString.split(":"));

                    /** Assign each attribute to a variable */
                    String fname = objectData.get(0);
                    String lname = objectData.get(1);
                    String number = objectData.get(2);
                    String stringArray = objectData.get(3);

                    /** Recreate the ByteArray to recreate the Bitmap image */
                    byte[] bitmapByteArray = Base64.decode(stringArray, Base64.DEFAULT);
                    Bitmap img = BitmapFactory.decodeByteArray(bitmapByteArray, 0, bitmapByteArray.length);
                    // Decoding of string into a byte array and into Bitmap learnt from Waqaslam retrieved at: https://stackoverflow.com/questions/16648706/android-convert-string-to-byte

                    if (img != null) {
                        /** Create the contact and add it to the list */
                        contacts.add(new Contact(fname, lname, number, img, getContext()));
                    }
                    else
                        contacts.add(new Contact(fname, lname, number, getContext()));
                }
            }

        }
        public void ClearSharedPreference()
        {
            SharedPreferences settings = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.clear();
            editor.commit();
        }
        public void RemoveContact(int position)
        {
            SharedPreferences settings = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();

            editor.putInt(COUNT_KEY, contacts.size());
            editor.remove(Integer.toString(position));
            editor.commit();
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {

            int id = item.getItemId();

            switch (id) {
                case R.id.action_help:
                    Toast.makeText(getContext(), "Help Menu", Toast.LENGTH_SHORT).show();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
                                        /** End of Fragment Class */
    }

    /**
     *  Fragment Pager Adapter for managing the fragment for each tab
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private List<PlaceholderFragment> Fragments;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            Fragments = new ArrayList();
        }

        /** Creates a new instance of the fragment class */
        @Override
        public PlaceholderFragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            PlaceholderFragment fragment = PlaceholderFragment.newInstance(position + 1);
            Fragments.add(fragment);
            return fragment;
        }

        /** Gets the current instance of the fragment */
        public PlaceholderFragment getInstance(int position ) {
            return Fragments.get(position);
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }
    }


}
