package com.veggfarm.android.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.veggfarm.android.R;

/**
 * created by Ashish Rawat
 */

public class SignUpActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("user").child("user1").child("number").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Toast.makeText(SignUpActivity.this, (String)snapshot.getValue(), Toast.LENGTH_SHORT).show();
                System.out.println(snapshot.getValue());  //prints "Do you have data? You'll love Firebase."
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    public void alreadyAccount(View view) {
    }

    public void SignUp(View view) {
        mDatabase.child("user").child("user1").child("number").setValue("8800523601");

    }
}
