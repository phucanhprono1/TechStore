package com.example.techstore.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.techstore.R;
import com.example.techstore.models.Category;
import com.example.techstore.models.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> products;
    private ProductClickListener listener;



    public ProductAdapter(List<Product> products, ProductAdapter.ProductClickListener listener){
        this.products = products;
        this.listener=listener;
        notifyDataSetChanged();
    }

    public void setProduct(List<Product> cate) {
        this.products = cate;
        notifyDataSetChanged();
    }


    @Override
    public ProductAdapter.ProductViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        return new ProductAdapter.ProductViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product,parent,false));
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        Glide.with(holder.itemView).load(products.get(position).getImage()).into(holder.thumb);
        holder.name.setText(products.get(position).getProductName());
        float fl = products.get(position).getPrice();
        String format = String.format("%.1f",fl);
        holder.price.setText(format);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(products.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        if(products.size()==0)return 0;
        return products.size();
    }



    static class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView thumb;
        TextView name ;
        TextView price ;
        public ProductViewHolder(View itemView) {
            super(itemView);
            this.thumb=itemView.findViewById(R.id.thumbImageProd);
            this.name =itemView.findViewById(R.id.productName);
            this.price = itemView.findViewById(R.id.productPrice);
        }
    }
    public interface ProductClickListener{
        public void onItemClick(Product product);
    }
}
