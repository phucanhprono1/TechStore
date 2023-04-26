package com.example.techstore.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.techstore.LocalNetwork;
import com.example.techstore.R;
import com.example.techstore.config.StaticConfig;
import com.example.techstore.models.CartItem;

import org.json.JSONObject;

import java.util.List;

public class ItemCartAdapter extends RecyclerView.Adapter<ItemCartAdapter.ItemViewHolder> {
    String url = new LocalNetwork().getUrl()+"/customer/cart/increase";
    String url2 = new LocalNetwork().getUrl()+"/customer/cart/decrease";
    String url3 = new LocalNetwork().getUrl()+"/customer/cart/remove";

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
        CartItem cit = cartItems.get(position);

        holder.name.setText(cartItems.get(position).getProduct().getProductName());
        Glide.with(holder.itemView).load(cartItems.get(position).getProduct().getImage()).into(holder.image);
        holder.price.setText(cartItems.get(position).getProduct().getPrice()+"");

        holder.quantity.setText(cartItems.get(position).getQuantity()+"");
        RequestQueue q = Volley.newRequestQueue(holder.itemView.getContext());


        holder.addMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addMoreClicklistener.onAddMoreClick(cartItems.get(position));

                JsonObjectRequest jor1 = new JsonObjectRequest(Request.Method.PUT, url + "/" + StaticConfig.UID + "/" + cit.getProduct().getProductId(), null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                q.add(jor1);
//                int m = cartItems.get(position).getQuantity();
//                m++;
//                holder.quantity.setText(m+"");
            }
        });
        holder.subtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subtractClickListener.onSubtractClick(cartItems.get(position));

                JsonObjectRequest jor2 = new JsonObjectRequest(Request.Method.PUT, url2 + "/" + StaticConfig.UID + "/" + cit.getProduct().getProductId(), null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                q.add(jor2);
//                int m = cartItems.get(position).getQuantity();
//                m--;
//                holder.quantity.setText(m+"");
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
