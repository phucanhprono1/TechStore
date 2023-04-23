package com.example.techstore.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.techstore.LocalNetwork;
import com.example.techstore.R;
import com.example.techstore.models.CartItem;

import java.util.List;

public class ItemCartAdapter extends RecyclerView.Adapter<ItemCartAdapter.ItemViewHolder> {
    String url = new LocalNetwork().getUrl()+"/customer/cart";

    private List<CartItem> cartItems;
    private AddMoreClickListener addMoreClicklistener;
    private SubtractClickListener subtractClickListener;


    public ItemCartAdapter(List<CartItem> cartItems, AddMoreClickListener addMoreClicklistener, SubtractClickListener subtractClickListener) {
        this.cartItems = cartItems;
        this.addMoreClicklistener = addMoreClicklistener;
        this.subtractClickListener = subtractClickListener;
        notifyDataSetChanged();
    }
    public void setItemCart(List<CartItem> cartItems) {
        this.cartItems = cartItems;
        notifyDataSetChanged();
    }

    @Override
    public ItemCartAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemCartAdapter.ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart,parent,false));
    }

    @Override
    public void onBindViewHolder(ItemCartAdapter.ItemViewHolder holder, int position) {
        holder.name.setText(cartItems.get(position).getProduct().getProductName());
        Glide.with(holder.itemView).load(cartItems.get(position).getProduct().getImage()).into(holder.image);
        holder.price.setText(cartItems.get(position).getProduct().getPrice()+"");
        holder.quantity.setText(cartItems.get(position).getQuantity()+"");
        RequestQueue q = Volley.newRequestQueue(holder.itemView.getContext());
        CartItem cit = cartItems.get(position);

        holder.addMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMoreClicklistener.onAddMoreClick(cartItems.get(position));

            }
        });
        holder.subtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subtractClickListener.onSubtractClick(cartItems.get(position));


            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView price;
        TextView quantity;
        ImageView image;
        ImageButton addMore;
        ImageButton subtract;
        public ItemViewHolder(View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.menuName);
            this.price = itemView.findViewById(R.id.menuPrice);
            this.quantity = itemView.findViewById(R.id.menuQty);
            this.image = itemView.findViewById(R.id.thumbImage);
            this.addMore = itemView.findViewById(R.id.addMore);
            this.subtract = itemView.findViewById(R.id.subtract);
        }
    }
    public interface AddMoreClickListener{
        void onAddMoreClick(CartItem product);
    }
    public interface SubtractClickListener{
        void onSubtractClick(CartItem product);
    }
}
