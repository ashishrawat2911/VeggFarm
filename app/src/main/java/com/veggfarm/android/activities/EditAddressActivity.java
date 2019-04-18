package com.veggfarm.android.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.veggfarm.android.R;
import com.veggfarm.android.network.NetworkConnection;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

/**
 * created by Ashish Rawat
 */

public class EditAddressActivity extends AppCompatActivity {
    EditText address, pincode;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_address);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Edit Address");
        address = findViewById(R.id.editAddress);
        pincode = findViewById(R.id.editPinCode);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        setDetails();
    }

    //fetching the address from the info in firebase database
    private void setDetails() {
        databaseReference.child("user")
                .child(mAuth.getUid())
                .child("info").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                address.setText("" + dataSnapshot.child("address").getValue());
                pincode.setText("" + dataSnapshot.child("pincode").getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditAddressActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void noInternetShow() {
        if (!NetworkConnection.isConnected(this)) {
            new LovelyStandardDialog(this)
                    .setTopColorRes(R.color.colorAccent)
                    .setCancelable(false)
                    .setIcon(R.drawable.ic_action_error)
                    //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
                    .setTitle("User Alert")
                    .setMessage("No network connection")
                    .setPositiveButton(android.R.string.ok, null).show();
        }
    }

    //update the address
    public void saveDetails(View view) {
        noInternetShow();
        if (checkValidation()) {
            mDatabase
                    .child("user")
                    .child(mAuth.getUid())
                    .child("info")
                    .child("address")
                    .setValue(address.getText().toString().trim());
            mDatabase
                    .child("user")
                    .child(mAuth.getUid())
                    .child("info")
                    .child("pincode")
                    .setValue(pincode.getText().toString().trim());
            finish();
        }

    }

    //validation for edit text fields
    private boolean checkValidation() {
        if (TextUtils.isEmpty(address.getText().toString())) {
            address.setError("Field is empty");
            return false;
        } else if (TextUtils.isEmpty(pincode.getText().toString())) {
            pincode.setError("Field is empty");
            return false;
        } else if (TextUtils.isEmpty(address.getText().toString()) && TextUtils.isEmpty(pincode.getText().toString())) {
            address.setError("Field is empty");
            pincode.setError("Field is empty");
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
