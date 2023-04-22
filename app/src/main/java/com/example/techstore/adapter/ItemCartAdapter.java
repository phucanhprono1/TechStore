package com.example.techstore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techstore.R;
import com.example.techstore.models.ItemCart;
import com.example.techstore.models.Product;

import java.util.List;

public class ItemCartAdapter extends RecyclerView.Adapter<ItemCartAdapter.ItemViewHolder> {

    private List<ItemCart> products;
    private AddMoreClickListener addMoreClicklistener;
    private SubtractClickListener subtractClickListener;


    public ItemCartAdapter(List<ItemCart> products, AddMoreClickListener addMoreClicklistener, SubtractClickListener subtractClickListener) {
        this.products = products;
        this.addMoreClicklistener = addMoreClicklistener;
        this.subtractClickListener = subtractClickListener;
        notifyDataSetChanged();
    }
    public void setItemCart(List<ItemCart> itc) {
        this.products = itc;
        notifyDataSetChanged();
    }

    @Override
    public ItemCartAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart,parent,false));
    }

    @Override
    public void onBindViewHolder(ItemCartAdapter.ItemViewHolder holder, int position) {
        holder.name.setText(products.get(position).getProduct().getProductName());
        holder.price.setText(products.get(position).getProduct().getPrice()+"");
        holder.quantity.setText(products.get(position).getQuantity()+"");
        holder.addMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMoreClicklistener.onAddMoreClick(products.get(position));

            }
        });
        holder.subtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subtractClickListener.onSubtractClick(products.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView price;
        TextView quantity;
        ImageButton addMore;
        ImageButton subtract;
        public ItemViewHolder(View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.name);
            this.price = itemView.findViewById(R.id.menuPrice);
            this.quantity = itemView.findViewById(R.id.menuQty);
            this.addMore = itemView.findViewById(R.id.addMore);
            this.subtract = itemView.findViewById(R.id.subtract);
        }
    }
    public interface AddMoreClickListener{
        void onAddMoreClick(ItemCart product);
    }
    public interface SubtractClickListener{
        void onSubtractClick(ItemCart product);
    }
}
