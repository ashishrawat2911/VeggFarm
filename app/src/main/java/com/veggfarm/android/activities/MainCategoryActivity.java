package com.veggfarm.android.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.veggfarm.android.R;
import com.veggfarm.android.adaptor.ViewPagerCategoryAdaptor;

/**
 * created by Ashish Rawat
 */

public class MainCategoryActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private SmartTabLayout mSmartTabLayout;
    private ViewPager mViewPager;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    TextView name, phoneNumber, address, circleAvatar;
    LinearLayout navigationLayout;
    NavigationView navigationView;
    String pincode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_category);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        name = headerView.findViewById(R.id.navHeadName);
        phoneNumber = headerView.findViewById(R.id.navHeadPhoneNumber);
        address = headerView.findViewById(R.id.navHeadAddress);
        circleAvatar = headerView.findViewById(R.id.circleAvatar);
        navigationLayout = headerView.findViewById(R.id.navigation_header_layout);
        navigationView.setNavigationItemSelectedListener(this);
        mSmartTabLayout = findViewById(R.id.tab_view_pager_fav);
        mViewPager = findViewById(R.id.view_pager_fav);
        mViewPager.setAdapter(new ViewPagerCategoryAdaptor(getSupportFragmentManager(), this));
        mSmartTabLayout.setViewPager(mViewPager);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //fetching the details of user to be displayed on navigation drawer
        getDrawerUserDetails();

    }

    private void getDrawerUserDetails() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("user").child(mAuth.getUid()).child("info").exists()) {
                    name.setVisibility(View.VISIBLE);
                    phoneNumber.setVisibility(View.VISIBLE);
                    address.setVisibility(View.VISIBLE);
                    navigationLayout.setVisibility(View.VISIBLE);
                    circleAvatar.setVisibility(View.VISIBLE);
                    navigationView.setVisibility(View.VISIBLE);
                    name.setText(dataSnapshot.child("user").child(mAuth.getUid()).child("info").child("name").getValue().toString());
                    address.setText(dataSnapshot.child("user").child(mAuth.getUid()).child("info").child("address").getValue().toString());
                    phoneNumber.setText(dataSnapshot.child("user").child(mAuth.getUid()).child("info").child("phoneNumber").getValue().toString());
                    circleAvatar.setText(getNameInitials(name.getText().toString()));
                    pincode = dataSnapshot.child("user").child(mAuth.getUid()).child("info").child("pincode").getValue().toString();
                } else {
                    name.setVisibility(View.GONE);
                    phoneNumber.setVisibility(View.GONE);
                    address.setVisibility(View.GONE);
                    navigationLayout.setVisibility(View.GONE);
                    circleAvatar.setVisibility(View.GONE);
                    navigationView.setVisibility(View.GONE);
                }
                if (!dataSnapshot.child("user").child(mAuth.getUid()).exists()) {
                    mAuth.signOut();
                    startActivity(new Intent(MainCategoryActivity.this, LoginActivity.class));
                    finish();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainCategoryActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getNameInitials(String fullName) {
        String initials = "";
        String[] name = fullName.trim().split(" ");
        for (String aName : name) {
            initials = initials + aName.charAt(0);
        }
        if (initials.length() == 1) {
            initials = initials + fullName.charAt(1);
        }
        if (initials.length() > 2) {
            initials = String.valueOf((new StringBuilder(initials).deleteCharAt(2)));
        }
        return initials.toUpperCase();


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_cart) {
            startActivity(new Intent(this, CartActivity.class));
        } else if (id == R.id.nav_visit_us) {
            startActivity(new Intent(this, VisitUsActivity.class));

        } else if (id == R.id.nav_help) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:+917065522865"));
            startActivity(intent);
        } else if (id == R.id.nav_order) {
            startActivity(new Intent(this, OrdersActivity.class));

        } else if (id == R.id.nav_account) {
            startActivity(new Intent(this, EditInfoActivity.class));

        } else if (id == R.id.nav_say_hi) {
            try {
                String text = "Hi, I am " + name.getText() + "\nfrom " + address.getText() + " \n" + pincode;
                String toNumber = "+917065522865"; // Replace with mobile phone number without +Sign or leading zeros.
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + toNumber + "&text=" + text));
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (id == R.id.nav_SignOut) {
            mAuth.signOut();
            startActivity(new Intent(MainCategoryActivity.this, LoginActivity.class));
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_category, menu);
        MenuItem item = menu.findItem(R.id.action_cart);
        MenuItemCompat.setActionView(item, R.layout.cart_icon_badge);
        RelativeLayout notifCount = (RelativeLayout) MenuItemCompat.getActionView(item);
        final TextView tv = notifCount.findViewById(R.id.actionbar_notifcation_textview);
        tv.setVisibility(View.GONE);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("user").child(mAuth.getUid()).child("cart");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    tv.setVisibility(View.GONE);
                } else {
                    tv.setVisibility(View.VISIBLE);
                    tv.setText("" + dataSnapshot.getChildrenCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainCategoryActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        notifCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainCategoryActivity.this, CartActivity.class));
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_cart:
                Toast.makeText(this, "cart clicked", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
