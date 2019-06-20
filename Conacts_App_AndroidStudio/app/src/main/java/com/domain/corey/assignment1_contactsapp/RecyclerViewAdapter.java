package com.domain.corey.assignment1_contactsapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    Context mContext;
    List<Contact> mData;
    Dialog mDialog;

    public RecyclerViewAdapter(Context context, List<Contact> contacts) {
        this.mContext = context;
        this.mData = contacts;
    }

    /** View Holder for the dialog pop-up menu */
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.item_contact, parent, false);
        final MyViewHolder vHolder = new MyViewHolder(v);

        /** Contact Dialog Initialisation */
        mDialog = new Dialog(mContext);
        mDialog.setContentView(R.layout.dialog_contact);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        /** On click listener for tapping an item in the list of contacts */
        vHolder.contactItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** Find the associated data */
                final TextView dialogName = mDialog.findViewById(R.id.dialog_name_id);
                TextView dialogPhone = mDialog.findViewById(R.id.dialog_number_id);
                ImageView dialogImg = mDialog.findViewById(R.id.dialog_img);
                /** and then set the display data to of that in the list */
                String FirstName = mData.get(vHolder.getAdapterPosition()).getFirstName();
                String LastName = mData.get(vHolder.getAdapterPosition()).getLastName();
                dialogName.setText(FirstName + " " + LastName);
                dialogPhone.setText(mData.get(vHolder.getAdapterPosition()).getNumber());
                dialogImg.setImageBitmap(mData.get(vHolder.getAdapterPosition()).getImageBitmap());

                /** Set edit contact button and check if it is pressed */
                ImageButton editContactButton = mDialog.findViewById(R.id.dialog_edit_contact);
                editContactButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), EditContact.class);

                        intent.putExtra("CONTACT_FIRSTNAME", mData.get(vHolder.getAdapterPosition()).getFirstName());
                        intent.putExtra("CONTACT_LASTNAME",  mData.get(vHolder.getAdapterPosition()).getLastName());
                        intent.putExtra("CONTACT_NUMBER",  mData.get(vHolder.getAdapterPosition()).getNumber());

                        ByteArrayOutputStream bs = new ByteArrayOutputStream();
                        mData.get(vHolder.getAdapterPosition()).getImageBitmap().compress(Bitmap.CompressFormat.PNG, 50, bs);
                        intent.putExtra("CONTACT_PHOTO", bs.toByteArray());

                        intent.putExtra("POSITION", vHolder.getAdapterPosition());

                        view.getContext().startActivity(intent);
                    }
                });

                /** Set the call button listener */
                Button dialogCall = mDialog.findViewById(R.id.dialog_button_call);
                dialogCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(mContext, "Dialing " + mData.get(vHolder.getAdapterPosition()).getFirstName(), Toast.LENGTH_SHORT).show();
                    }
                });
                /** Set the message button listener */
                Button dialogMessage = mDialog.findViewById(R.id.dialog_button_message);
                dialogMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(mContext, "Message " + mData.get(vHolder.getAdapterPosition()).getFirstName(), Toast.LENGTH_SHORT).show();
                    }
                });

                /** Display the dialog */
                mDialog.show();
            }
        });

        return vHolder;
    }

    /** Sets the values of each item in the Recycler View for all positions in the list */
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String FirstName = mData.get(position).getFirstName();
        String LastName = mData.get(position).getLastName();

        holder.tv_name.setText(FirstName + " " + LastName);
        holder.tv_phone.setText(mData.get(position).getNumber());
        holder.img.setImageBitmap(mData.get(position).getImageBitmap());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    /** Filter the list data for the recycler view for when the user searches */
    public void filterList(ArrayList<Contact> filteredList) {
        mData = filteredList;
        notifyDataSetChanged();
    }

    /** View Holder class for holding the widget items */
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout contactItem;
        private TextView tv_name;
        private TextView tv_phone;
        private ImageView img;

        public MyViewHolder(View itemView) {
            super(itemView);

            contactItem = itemView.findViewById(R.id.contact_item);
            tv_name = itemView.findViewById(R.id.name_contact);
            tv_phone = itemView.findViewById(R.id.phone_contact);
            img = itemView.findViewById(R.id.img_contact);
        }
    }
}
