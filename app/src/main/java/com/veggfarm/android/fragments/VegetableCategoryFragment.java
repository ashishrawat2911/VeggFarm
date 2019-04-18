package com.veggfarm.android.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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

import java.util.ArrayList;
import java.util.List;


/**
 * created by Ashish Rawat
 */

public class VegetableCategoryFragment extends Fragment {
    FirebaseDatabase database;
    DatabaseReference myRef;
    RecyclerView categoriesRecyclerView;
    CategoriesAdaptor categoriesAdaptor;
    ProgressBar progressBar;
    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;

    public VegetableCategoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        //instantiate RecyclerView
        categoriesRecyclerView = view.findViewById(R.id.categories_recycler_view);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        progressBar = view.findViewById(R.id.itemCategoryProgressBar);

        database = FirebaseDatabase.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference().child("item").child("vegetables");
        loadList();


        //creating a new list


        //setting the layout of RecyclerView as Grid
        return view;
    }

    private void loadList() {
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