package com.veggfarm.android.adaptor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.veggfarm.android.R;
import com.veggfarm.android.model.Cart;

import java.util.List;


/**
 * created by Ashish Rawat
 */

public class OrderDetailsAdaptor extends RecyclerView.Adapter<OrderDetailsAdaptor.MyViewHolder> {
    private Context ctx;
    private List<Cart> orderItemList;

    public OrderDetailsAdaptor(Context ctx, List<Cart> orderItemList) {
        this.ctx = ctx;
        this.orderItemList = orderItemList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        v = LayoutInflater.from(ctx).inflate(R.layout.order_details_item, viewGroup, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        myViewHolder.totalNumber.setText("X " + orderItemList.get(position).getTotalNumber());
        myViewHolder.cost.setText("Rs " + orderItemList.get(position).getCost() + " /" + orderItemList.get(position).getUnit());
        myViewHolder.itemName.setText(orderItemList.get(position).getItemName());
        myViewHolder.marketPrice.setText("Rs " + orderItemList.get(position).getMarketPrice());
        Glide.with(ctx)
                .load(orderItemList.get(position).getItemImage())
                .into(myViewHolder.itemImage);

    }

    @Override
    public int getItemCount() {
        return orderItemList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, marketPrice, cost, totalNumber;
        ImageView itemImage;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.order_details_title_text_view);
            itemImage = itemView.findViewById(R.id.order_details_image_view);
            marketPrice = itemView.findViewById(R.id.order_details_market_price);
            cost = itemView.findViewById(R.id.order_details_item_rate);
            totalNumber = itemView.findViewById(R.id.order_details_total_number);

        }
    }

}
