package com.veggfarm.android.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.veggfarm.android.R;
import com.veggfarm.android.adaptor.CategoriesAdaptor;
import com.veggfarm.android.model.Items;
import com.veggfarm.android.network.NetworkConnection;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.util.ArrayList;
import java.util.List;


/**
 * created by Ashish Rawat
 */
public class FruitsCategoryFragment extends Fragment {

    DatabaseReference myRef;

    ProgressBar progressBar;

    RecyclerView categoriesRecyclerView;
    CategoriesAdaptor categoriesAdaptor;


    public FruitsCategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        //instantiate RecyclerView
        categoriesRecyclerView = view.findViewById(R.id.categories_recycler_view);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        progressBar = view.findViewById(R.id.itemCategoryProgressBar);
        myRef = FirebaseDatabase.getInstance().getReference().child("item").child("fruits");
        myRef.keepSynced(true);

        loadData();
        //creating a new list

        return view;
    }

    public void noInternetShow() {
        if (!NetworkConnection.isConnected(getContext())) {
            new LovelyStandardDialog(getActivity())
                    .setTopColorRes(R.color.colorAccent)
                    .setCancelable(false)
                    .setIcon(R.drawable.ic_action_error)
                    //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
                    .setTitle("User Alert")
                    .setMessage("No network connection")
                    .setPositiveButton(android.R.string.ok, null).show();
        }
    }

    private void loadData() {
        noInternetShow();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                progressBar.setVisibility(View.VISIBLE);
                List<Items> categoriesList = new ArrayList<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Items items = dataSnapshot1.getValue(Items.class);
                    Log.e("Fire base data change ", "onDataChange: " + items.getItemName());
                    categoriesList.add(items);
                }
                progressBar.setVisibility(View.GONE);
                categoriesAdaptor = new CategoriesAdaptor(getContext(), categoriesList);
                categoriesRecyclerView.setAdapter(categoriesAdaptor);
                categoriesAdaptor.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}