package com.veggfarm.android.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.veggfarm.android.R;
import com.veggfarm.android.model.Cart;
import com.veggfarm.android.model.Items;

import java.util.List;

/**
 * created by Ashish Rawat
 */

public class CategoriesAdaptor extends RecyclerView.Adapter<CategoriesAdaptor.MyViewHolder> {
    private Context ctx;
    private List<Items> categoriesList;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference updateValueDatabase;
    Toast toast;

    public CategoriesAdaptor(Context ctx, List<Items> categoriesList) {
        this.ctx = ctx;
        this.categoriesList = categoriesList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(ctx).inflate(R.layout.categories_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        Glide.with(ctx)
                .load(categoriesList.get(position).getItemImage())
                .into(holder.categoriesImage);
        holder.categoriesTitle.setText(categoriesList.get(position).getItemName());
        holder.marketPrice.setText("Rs " + categoriesList.get(position).getMarketPrice() + " / " + categoriesList.get(position).getUnit());
        holder.itemRate.setText("Rs " + categoriesList.get(position).getCost() + " / " + categoriesList.get(position).getUnit());
        holder.marketPrice.setPaintFlags(holder.marketPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.offPercentTextView.setText(offPercent(categoriesList.get(position).getCost(), categoriesList.get(position).getMarketPrice()) + "% off");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("user").child(mAuth.getUid()).child("cart").child(categoriesList.get(position).getItemName()).exists()) {
                    holder.addToCartButton.setText(R.string.added_to_cart);
                    holder.addToCartButton.setEnabled(false);
                } else {
                    holder.addToCartButton.setText(R.string.add);
                    holder.addToCartButton.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView categoriesTitle, itemRate, marketPrice;
        ImageView categoriesImage;
        Button addToCartButton;
        TextView offPercentTextView;

        MyViewHolder(final View itemView) {
            super(itemView);
            mDatabase = FirebaseDatabase.getInstance().getReference();
            updateValueDatabase = FirebaseDatabase.getInstance().getReference();
            mAuth = FirebaseAuth.getInstance();
            categoriesTitle = itemView.findViewById(R.id.categories_title_text_view);
            categoriesImage = itemView.findViewById(R.id.categories_image_view);
            marketPrice = itemView.findViewById(R.id.categories_market_price);
            itemRate = itemView.findViewById(R.id.item_rate);
            addToCartButton = itemView.findViewById(R.id.addToCart);
            offPercentTextView = itemView.findViewById(R.id.categories_off_percent);
            addToCartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.child("user").child(mAuth.getUid()).child("cart").child(categoriesList.get(getAdapterPosition()).getItemName()).exists()) {
                                Cart cart = new Cart(categoriesList.get(getAdapterPosition()).getCost(),
                                        categoriesList.get(getAdapterPosition()).getItemId(),
                                        categoriesList.get(getAdapterPosition()).getItemName(),
                                        categoriesList.get(getAdapterPosition()).getItemImage()
                                        , categoriesList.get(getAdapterPosition()).getMarketPrice(),
                                        1,
                                        categoriesList.get(getAdapterPosition()).getUnit());
                                try {
                                    setItemStatus(cart);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                showToast(categoriesList.get(getAdapterPosition()).getItemName() + " Added to cart");

                            } else
                                showToast(categoriesList.get(getAdapterPosition()).getItemName() + " Already Added ");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(ctx, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            Toast.makeText(ctx, databaseError.getDetails(), Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            });
        }

        private void setItemStatus(Cart cart) throws Exception {
            mDatabase.child("user")
                    .child(mAuth.getUid())
                    .child("cart")
                    .child(categoriesList.get(getAdapterPosition()).getItemName())
                    .setValue(cart);
        }

    }


    private int offPercent(Double cost, Double marketPrice) {
        int offPercent;
        offPercent = (int) (((marketPrice - cost) / marketPrice) * 100);
        return offPercent;
    }

    private void showToast(String message) {
        if (toast != null) toast.cancel();
        toast = Toast.makeText(ctx, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}

