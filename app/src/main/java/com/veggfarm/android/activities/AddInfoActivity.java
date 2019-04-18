package com.veggfarm.android.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.veggfarm.android.R;
import com.veggfarm.android.model.Info;
import com.veggfarm.android.model.ReqInfo;
import com.veggfarm.android.network.NetworkConnection;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

/**
 * created by Ashish Rawat
 */

public class AddInfoActivity extends AppCompatActivity {
    EditText name, email, address, pincode;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    AlertDialog.Builder alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Add Personal Info");
        setFindViewById();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        setProgressDialog();
        Toast.makeText(this, "uid " + mAuth.getUid(), Toast.LENGTH_SHORT).show();

    }

    private void setFindViewById() {
        //Instantiate the reference of feilds
        name = findViewById(R.id.addInfoName);
        email = findViewById(R.id.addInfoEmail);
        address = findViewById(R.id.addInfoAddress);
        pincode = findViewById(R.id.addInfoPincode);

    }

    public void saveDetails(View view) {
        noInternetShow();
        progressDialog.show();
        try {
            saveToFirebaseDatabase(
                    name.getText().toString(),
                    email.getText().toString(),
                    address.getText().toString(),
                    pincode.getText().toString()
            );
        } catch (Exception e) {
            progressDialog.dismiss();
            Log.e("Details not Saved", "saveDetails: " + e.getMessage());
            Toast.makeText(this, "Details not Saved", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    //saving details to the firebase database
    private void saveToFirebaseDatabase(String name, String email, String address, String pincode) {
        //TODO ALSO ADD UID IN INFO
        Info info = new Info(name, email, address, pincode, mAuth.getCurrentUser().getPhoneNumber());
        Toast.makeText(this, "" + info, Toast.LENGTH_SHORT).show();
        mDatabase.child("user").child(mAuth.getUid()).child("info").setValue(info).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                setAlertDialog();
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

    private void setProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading.....");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
    }

    private void setAlertDialog() {
        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Request Submitted");
        alertDialog.setMessage("Thanks for Requesting us\nOur team will get back to you");
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                startActivity(new Intent(AddInfoActivity.this, MainCategoryActivity.class));
            }
        });
        alertDialog.setCancelable(false);
        alertDialog.setIcon(R.drawable.ic_action_about);
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mAuth.signOut();
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
