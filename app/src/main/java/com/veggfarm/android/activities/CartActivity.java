package com.veggfarm.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.veggfarm.android.R;
import com.veggfarm.android.adaptor.CartAdaptor;
import com.veggfarm.android.model.Cart;
import com.veggfarm.android.network.NetworkConnection;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * created by Ashish Rawat
 */

public class CartActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    TextView noOfItems, totalCost;
    RecyclerView cartRecyclerView;
    CartAdaptor cartAdaptor;
    List<Cart> categoriesList = new ArrayList<>();
    Button cartContinueButton;
    LinearLayout linearLayout;
    Double totalItemCost;
    ConstraintLayout bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        //Setting title on app bar
        setTitle("Cart");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //instantiate reference of fields
        noOfItems = findViewById(R.id.no_of_items);
        totalCost = findViewById(R.id.cartTotalPrice);
        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        cartContinueButton = findViewById(R.id.cartContinueButton);
        linearLayout = findViewById(R.id.noItemsInCart);
        bottomBar = findViewById(R.id.constraintLayoutBottomBar);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartRecyclerView.setHasFixedSize(true);
        mAuth = FirebaseAuth.getInstance();
        //getting the instance for cart in firebase database
        mDatabase = FirebaseDatabase.getInstance().getReference().child("user").child(mAuth.getUid()).child("cart");
        mDatabase.keepSynced(true);

        //perform swipe operation for the cart recyclerView
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //delete the item when swiped
                try {
                    cartAdaptor.removeFromList(viewHolder.getAdapterPosition());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).attachToRecyclerView(cartRecyclerView);
        noInternetShow();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Double cost = 0D;
                categoriesList.clear();
                //fetching the list of items from the cart in firebase database
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Cart cart = dataSnapshot1.getValue(Cart.class);
                    cost = cost + cart.getCost() * cart.getTotalNumber();
                    categoriesList.add(cart);
                }

                if (categoriesList.size() == 0) {
                    setTitle("Cart");
                    bottomBar.setVisibility(View.GONE);
                    cartRecyclerView.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);
                    cartContinueButton.setEnabled(false);
                    noOfItems.setText("0");
                    totalCost.setText("Rs 0");
                } else {
                    bottomBar.setVisibility(View.VISIBLE);
                    cartRecyclerView.setVisibility(View.VISIBLE);
                    linearLayout.setVisibility(View.GONE);
                    cartContinueButton.setEnabled(true);
                    setTitle("Cart (" + categoriesList.size() + ")");
                    if (categoriesList.size() > 1) {
                        noOfItems.setText("" + categoriesList.size() + " items");
                    } else if (categoriesList.size() == 1) {
                        noOfItems.setText("" + categoriesList.size() + " item");
                    }

                    totalItemCost = cost;
                    totalCost.setText("Rs " + cost);
                }
                cartAdaptor.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CartActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        cartAdaptor = new CartAdaptor(CartActivity.this, categoriesList);
        cartRecyclerView.setAdapter(cartAdaptor);
    }


    public void placeOrder(View view) {
        Intent i = new Intent(CartActivity.this, OrderProcessActivity.class);
        startActivity(i);

    }


    public void startShopping(View view) {
        startActivity(new Intent(this, MainCategoryActivity.class));
        finish();
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
