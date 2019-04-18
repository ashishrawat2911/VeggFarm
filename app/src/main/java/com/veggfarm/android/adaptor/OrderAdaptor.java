package com.veggfarm.android.adaptor;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.veggfarm.android.R;
import com.veggfarm.android.activities.OrderDetailsActivity;
import com.veggfarm.android.model.OrderItem;
import com.veggfarm.android.utils.Constants;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * created by Ashish Rawat
 */

public class OrderAdaptor extends RecyclerView.Adapter<OrderAdaptor.MyViewHolder> {
    private Context ctx;
    private List<OrderItem> orderItemList;

    public OrderAdaptor(Context ctx, List<OrderItem> orderItemList) {
        this.ctx = ctx;
        this.orderItemList = orderItemList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        v = LayoutInflater.from(ctx).inflate(R.layout.order_tem, viewGroup, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        Collections.sort(orderItemList, new Comparator<OrderItem>() {
            @Override
            public int compare(OrderItem o1, OrderItem o2) {
                return o2.getOrderTime().compareTo(o1.getOrderTime());
            }
        });
        myViewHolder.totalNumber.setText(orderItemList.get(position).getTotalNumber() + " items");
        myViewHolder.orderStatusTextView.setText(orderItemList.get(position).getOrderStatus());
        myViewHolder.orderNumber.setText("Order #" + orderItemList.get(position).getOrderNumber());
        myViewHolder.orderAmount.setText("Rs " + orderItemList.get(position).getOrderAmount());
        if (orderItemList.get(position).getOrderStatus().trim().toLowerCase().equals("delivered")) {
            myViewHolder.orderStatusImage.setImageResource(R.drawable.ic_check);
        } else myViewHolder.orderStatusImage.setImageResource(R.drawable.ic_clock);
    }

    @Override
    public int getItemCount() {
        return orderItemList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView orderNumber, orderAmount, orderStatusTextView, totalNumber;
        ImageView orderStatusImage;

        CardView cardView;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view_show_card);
            orderNumber = itemView.findViewById(R.id.order_number);
            orderStatusImage = itemView.findViewById(R.id.order_status_image);
            orderAmount = itemView.findViewById(R.id.order_amount);
            orderStatusTextView = itemView.findViewById(R.id.order_status_TextView);
            totalNumber = itemView.findViewById(R.id.order_items_no);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(ctx, OrderDetailsActivity.class);
                    i.putExtra(Constants.ORDER_NUMBER, orderItemList.get(getAdapterPosition()).getOrderNumber());
                    ctx.startActivity(i);
                }
            });
        }
    }

}
