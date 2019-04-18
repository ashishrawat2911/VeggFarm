package com.veggfarm.android.adaptor;

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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.veggfarm.android.R;
import com.veggfarm.android.model.Cart;

import java.util.List;


/**
 * created by Ashish Rawat
 */

public class CartAdaptor extends RecyclerView.Adapter<CartAdaptor.MyViewHolder> {
    private Context ctx;
    private List<Cart> cartList;
    private DatabaseReference mDatabase;
    private DatabaseReference updateValueDatabase;
    private FirebaseAuth mAuth;
    Toast toast;

    public CartAdaptor(Context ctx, List<Cart> cartList) {
        this.ctx = ctx;
        this.cartList = cartList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        v = LayoutInflater.from(ctx).inflate(R.layout.cart_tem, viewGroup, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {

        myViewHolder.categoriesTitle.setText(cartList.get(position).getItemName());
        Glide.with(ctx)
                .load(cartList.get(position).getItemImage())
                .into(myViewHolder.categoriesImage);
        myViewHolder.itemRate.setText("Rs " + cartList.get(position).getCost() + " / " + cartList.get(position).getUnit());
        myViewHolder.noOfItems.setText("" + cartList.get(position).getTotalNumber());
        myViewHolder.marketPrice.setText("Rs " + cartList.get(position).getMarketPrice() + " / " + cartList.get(position).getUnit());
        myViewHolder.marketPrice.setPaintFlags(myViewHolder.marketPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    private void updateList(final int position, boolean b) throws Exception {

        try {
            Log.e("lissssssssss", "updateList: " + cartList.get(position).getItemName() + cartList.get(position).getTotalNumber());
            updateValueDatabase = FirebaseDatabase.getInstance().getReference().child("user")
                    .child(mAuth.getUid())
                    .child("cart")
                    .child(cartList.get(position).getItemName())
                    .child("totalNumber");
            if (b) {
                updateValueDatabase.setValue(cartList.get(position).getTotalNumber() + 1).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
            } else {
                updateValueDatabase.setValue(cartList.get(position).getTotalNumber() - 1).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });


            }
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            Log.e("hueeeeeeee", "updateList fault ");

        }
        notifyItemChanged(position);
    }

    public void removeFromList(int position) throws Exception {
        String itemName = cartList.get(position).getItemName();
        mDatabase.child("user")
                .child(mAuth.getUid())
                .child("cart")
                .child(itemName)
                .setValue(null);
        cartList.remove(position);
        showToast(itemName + " removed from cart");
        notifyItemRemoved(position);
    }

 /*   public List<Cart> getcartList() {
        return cartList;
    }*/

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView categoriesTitle, itemRate, noOfItems, marketPrice;
        ImageView categoriesImage;
        Button add, subtract;


        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mDatabase = FirebaseDatabase.getInstance().getReference();
            updateValueDatabase = FirebaseDatabase.getInstance().getReference();
            mAuth = FirebaseAuth.getInstance();
            categoriesTitle = itemView.findViewById(R.id.cart_title_text_view);
            categoriesImage = itemView.findViewById(R.id.cart_image_view);
            itemRate = itemView.findViewById(R.id.cart_item_rate);
            noOfItems = itemView.findViewById(R.id.cartNumber_of_items);
            add = itemView.findViewById(R.id.cartAdd_value);
            marketPrice = itemView.findViewById(R.id.cart_market_price);
            subtract = itemView.findViewById(R.id.cartSubtractValue);

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        updateList(getAdapterPosition(), true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            subtract.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int totalNumber = cartList.get(getAdapterPosition()).getTotalNumber();
                        if (totalNumber <= 1) {
                            removeFromList(getAdapterPosition());
                        } else {
                            updateList(getAdapterPosition(), false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    }

    private void showToast(String message) {
        if (toast!=null)toast.cancel();
        toast = Toast.makeText(ctx, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
