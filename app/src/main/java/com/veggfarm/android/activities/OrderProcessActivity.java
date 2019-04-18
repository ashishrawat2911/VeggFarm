package com.veggfarm.android.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.veggfarm.android.R;
import com.veggfarm.android.adaptor.CartAdaptor;
import com.veggfarm.android.model.Cart;
import com.veggfarm.android.model.OrderItem;
import com.veggfarm.android.network.NetworkConnection;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * created by Ashish Rawat
 */

public class OrderProcessActivity extends AppCompatActivity {
    ProgressDialog progressDialog;
    AlertDialog.Builder alertDialog;
    TextView priceNoOfItems, cost, finalCost, address;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, confirmDatabase, cartDatabase;
    RecyclerView orderProcessRecyclerView;
    CartAdaptor cartAdaptor;
    List<Cart> cartList = new ArrayList<>();
    Double inCost = 0D;
    OrderItem.Address addressOrder;
    boolean orderPlaced = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_process);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Delivery");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        confirmDatabase = FirebaseDatabase.getInstance().getReference();
        cartDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        priceNoOfItems = findViewById(R.id.priceNoOfItems);
        cost = findViewById(R.id.orderPrice);
        finalCost = findViewById(R.id.orderTotalValue);
        address = findViewById(R.id.orderProcessAddress);
        orderProcessRecyclerView = findViewById(R.id.orderProcessRecyclerView);
        orderProcessRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderProcessRecyclerView.setHasFixedSize(true);

        setProgressDialog();

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                try {
                    cartAdaptor.removeFromList(viewHolder.getAdapterPosition());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).attachToRecyclerView(orderProcessRecyclerView);
        loadList();
        loadAddress();
    }

    public void confirmOrder(View view) {
        setAlertDialog();
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

    private void orderConfirmation() throws Exception {
        noInternetShow();
        progressDialog.show();
        Date c = Calendar.getInstance().getTime();
        String itemNumber = (int) Math.round(inCost) +
                cartList.size() * (int) Math.round(Math.random()) + "" + (int) Math.round(Math.random()) +
                System.currentTimeMillis();
        OrderItem orderItem = new OrderItem(addressOrder,
                inCost,
                itemNumber,
                "waiting",
                c.toString(),
                cartList.size(),
                cartList,
                "Cash on Delivery", mAuth.getUid());
        // cartDatabase.child("user").child(mAuth.getUid()).child("order").setValue(null);
        confirmDatabase
                .child("order")
                .child(itemNumber)
                .setValue(orderItem).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                orderPlaced = true;
                cartDatabase.child("user").child(mAuth.getUid()).child("cart").setValue(null)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(OrderProcessActivity.this, "Order Placed", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(OrderProcessActivity.this, MainCategoryActivity.class));
                                finish();
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void setProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Order Processing");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
    }

    private void setAlertDialog() {
        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Confirm Order");
        alertDialog.setMessage("Do you confirm this Order?");
        alertDialog.setIcon(R.drawable.ic_action_cart);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    orderConfirmation();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.show();
    }

    private void loadList() {
        noInternetShow();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("user").child(mAuth.getUid()).child("cart");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Double inCartCost = 0D;
                cartList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Cart cart = dataSnapshot1.getValue(Cart.class);
                    cartList.add(cart);
                    inCartCost = inCartCost + (cart.getCost() * cart.getTotalNumber());
                }
                inCost = inCartCost;
                if (cartList.size() == 0 && !orderPlaced) {
                    finish();
                }
                setTitle("Delivery (" + cartList.size() + ")");
                if (cartList.size() > 1) {
                    priceNoOfItems.setText("Price ( " + cartList.size() + " items )");
                } else {
                    priceNoOfItems.setText("Price ( " + cartList.size() + " item )");
                }
                cost.setText("Rs " + inCartCost);
                finalCost.setText("Rs " + inCartCost);
                cartAdaptor = new CartAdaptor(OrderProcessActivity.this, cartList);
                orderProcessRecyclerView.setAdapter(cartAdaptor);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(OrderProcessActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAddress() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("user").child(mAuth.getUid()).child("info");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                address.setText("" + dataSnapshot.child("address").getValue() + "\n" + dataSnapshot.child("pincode").getValue());
                addressOrder = new OrderItem.Address(dataSnapshot.child("name").getValue().toString(),
                        dataSnapshot.child("address").getValue().toString(),
                        dataSnapshot.child("pincode").getValue().toString(),
                        dataSnapshot.child("phoneNumber").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(OrderProcessActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void editAddress(View view) {
        startActivity(new Intent(OrderProcessActivity.this, EditAddressActivity.class));
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
