package com.veggfarm.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.veggfarm.android.R;
import com.veggfarm.android.adaptor.OrderDetailsAdaptor;
import com.veggfarm.android.model.Cart;
import com.veggfarm.android.network.NetworkConnection;
import com.veggfarm.android.utils.Constants;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailsActivity extends AppCompatActivity {
    TextView orderId, orderPrice, orderStatus, userName, userAddress, userPincode, totalItems, orderCost, totalOrderCost, paymentMode;
    ImageView orderStatusImage;
    RecyclerView orderStatusRecyclerView;
    OrderDetailsAdaptor orderDetailsAdaptor;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Order Details");
        setFindViewById();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        Intent i = getIntent();
        String orderNumber = i.getStringExtra(Constants.ORDER_NUMBER);
        loadDetails(orderNumber);
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

    private void loadDetails(final String orderNumber) {
        noInternetShow();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Cart> cartList = new ArrayList<>();
                DataSnapshot dataSnapshot1 = dataSnapshot.child("order").child(orderNumber);
                orderId.setText("ORDER ID : " + dataSnapshot1.child("orderNumber").getValue());
                orderPrice.setText("Rs " + dataSnapshot1.child("orderAmount").getValue());
                orderCost.setText("Rs " + dataSnapshot1.child("orderAmount").getValue());
                totalOrderCost.setText("Rs " + dataSnapshot1.child("orderAmount").getValue());
                totalItems.setText("Price( " + dataSnapshot1.child("totalNumber").getValue() + " )");
                paymentMode.setText((CharSequence) dataSnapshot1.child("paymentMode").getValue());
                orderStatus.setText((CharSequence) dataSnapshot1.child("orderStatus").getValue());
                userName.setText((CharSequence) dataSnapshot1.child("address").child("name").getValue());
                userAddress.setText((CharSequence) dataSnapshot1.child("address").child("address").getValue());
                userPincode.setText((CharSequence) dataSnapshot1.child("address").child("pincode").getValue());

                if (dataSnapshot1.child("orderStatus").getValue().equals("delivered")) {
                    orderStatusImage.setImageResource(R.drawable.ic_check);
                } else {
                    orderStatusImage.setImageResource(R.drawable.ic_clock);
                }

                DataSnapshot dataSnapshot2 = dataSnapshot.child("order").child(orderNumber).child("cartList");
                for (DataSnapshot dataSnapshot3 : dataSnapshot2.getChildren()) {
                    Cart cart = dataSnapshot3.getValue(Cart.class);
                    cartList.add(cart);
                }
                Toast.makeText(OrderDetailsActivity.this, "" + cartList.size(), Toast.LENGTH_SHORT).show();
                orderDetailsAdaptor = new OrderDetailsAdaptor(OrderDetailsActivity.this, cartList);
                orderStatusRecyclerView.setAdapter(orderDetailsAdaptor);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setFindViewById() {
        orderId = findViewById(R.id.orderId);
        orderPrice = findViewById(R.id.orderPrice);
        orderStatus = findViewById(R.id.orderStatus);
        userName = findViewById(R.id.userName);
        userAddress = findViewById(R.id.userAddress);
        userPincode = findViewById(R.id.userPinCode);
        totalItems = findViewById(R.id.priceNoOfItems);
        orderCost = findViewById(R.id.orderPriceDetails);
        totalOrderCost = findViewById(R.id.orderTotalValue);
        paymentMode = findViewById(R.id.paymentStatus);
        orderStatusImage = findViewById(R.id.orderStatusImage);
        orderStatusRecyclerView = findViewById(R.id.orderDetailsRecyclerView);
        orderStatusRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderStatusRecyclerView.setHasFixedSize(true);
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
