package com.veggfarm.android.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.veggfarm.android.R;
import com.veggfarm.android.adaptor.OrderAdaptor;
import com.veggfarm.android.model.OrderItem;
import com.veggfarm.android.network.NetworkConnection;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * created by Ashish Rawat
 */

public class OrdersActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    ProgressDialog progressDialog;
    RecyclerView ordersRecyclerView;
    OrderAdaptor cartAdaptor;
    List<OrderItem> orderItemList = new ArrayList<>();
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("My Orders");
        ordersRecyclerView = findViewById(R.id.ordersRecyclerView);
        linearLayout = findViewById(R.id.noItemsInOrder);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ordersRecyclerView.setHasFixedSize(true);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("order");
        mDatabase.keepSynced(true);
        setProgressDialog();
        progressDialog.show();
        loadOrders();
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

    private void loadOrders() {
        noInternetShow();
        mDatabase.orderByChild("uID").equalTo(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                progressDialog.show();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    OrderItem orderItem = dataSnapshot1.getValue(OrderItem.class);
                    orderItemList.add(orderItem);
                }
                if (orderItemList.size() == 0) {
                    ordersRecyclerView.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);

                } else {
                    ordersRecyclerView.setVisibility(View.VISIBLE);
                    linearLayout.setVisibility(View.GONE);

                }
                cartAdaptor.notifyDataSetChanged();
                if (progressDialog != null)
                    progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(OrdersActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                if (progressDialog != null)
                    progressDialog.dismiss();
            }
        });
        cartAdaptor = new OrderAdaptor(OrdersActivity.this, orderItemList);
        ordersRecyclerView.setAdapter(cartAdaptor);
    }

    public void startShopping(View view) {
        startActivity(new Intent(this, MainCategoryActivity.class));
        finish();
    }

    private void setProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Order Processing");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
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
