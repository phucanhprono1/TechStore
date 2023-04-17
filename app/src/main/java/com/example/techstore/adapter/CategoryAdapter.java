package com.example.techstore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techstore.R;
import com.example.techstore.models.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>{

    private CategoryClickListener categoryClickListener;
    private List<Category> cate;
    public CategoryAdapter(List<Category> cate,CategoryClickListener categoryClickListener){
        this.cate = cate;
        this.categoryClickListener=categoryClickListener;
        notifyDataSetChanged();
    }

    public void setCate(List<Category> cate) {
        this.cate = cate;
        notifyDataSetChanged();
    }


    @Override
    public CategoryAdapter.CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category,parent,false));
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        holder.name.setText(cate.get(position).getCategoryName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryClickListener.onItemClick(cate.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return cate.size();
    }



    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView name ;
        public CategoryViewHolder(View itemView) {
            super(itemView);
            this.name =itemView.findViewById(R.id.categor);
        }
    }
    public interface CategoryClickListener{
        public void onItemClick(Category cate);
    }
}
