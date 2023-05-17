package com.example.techstore.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.techstore.R;
import com.example.techstore.models.OrderItem;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class OrderItemsAdapter extends RecyclerView.Adapter<OrderItemsAdapter.OrderItemViewHolder> {
    private List<OrderItem> orderItems;

    public OrderItemsAdapter(List<OrderItem> orderItems) {

        this.orderItems = orderItems;
        notifyDataSetChanged();
    }


    @Override
    public OrderItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_item_product_layout, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder( OrderItemViewHolder holder, int position) {
        OrderItem orderItem = orderItems.get(position);
        holder.productNameTextView.setText(orderItem.getProduct().getProductName());
        holder.productPriceTextView.setText(String.format(Locale.getDefault(), "%.2f$", orderItem.getProduct().getPrice()));
        holder.productQuantityTextView.setText(String.format(Locale.getDefault(), "x%d", orderItem.getQuantity()));
        // Load image from url using Picasso library
        Picasso.get().load(orderItem.getProduct().getImage()).into(holder.productImageView);
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    public static class OrderItemViewHolder extends RecyclerView.ViewHolder {

        ImageView productImageView;
        TextView productNameTextView;
        TextView productPriceTextView;
        TextView productQuantityTextView;

        public OrderItemViewHolder(View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.product_image_view);
            productNameTextView = itemView.findViewById(R.id.product_name_text_view);
            productPriceTextView = itemView.findViewById(R.id.product_price_text_view);
            productQuantityTextView = itemView.findViewById(R.id.product_quantity_text_view);
        }
    }
}
