package com.example.techstore.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.techstore.R;
import com.example.techstore.models.Comment;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder>  {

    private ItemClickListener mClickListener;
    private List<Comment> comm = new ArrayList<>();
    private LayoutInflater mInflater;
    public CommentAdapter(Context context, List<Comment> comm){
        this.mInflater = LayoutInflater.from(context);
        this.comm.clear();
        this.comm.addAll(comm);
    }

//    // data is passed into the constructor
//    CommentAdapter(Context context, List<String> data) {
//        this.mInflater = LayoutInflater.from(context);
//        this.mData = data;
//    }


    public  void setComm(List<Comment> comment) {
        comm.clear();
        comm.addAll(comment);
        notifyDataSetChanged();


    }
    @Override
    public CommentViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        return new CommentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment,parent,false));
    }





    @Override
    public void onBindViewHolder(CommentViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(comm.get(holder.getAdapterPosition()).getComment(("productId")));
        holder.response.setText(comm.get(holder.getAdapterPosition()).getResp_comment(("productId")));

      //  holder.name.setText("1111111");
        //Log.d("productId::",comm.get(holder.getAdapterPosition()).getComment(("productId")));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(view,position);
            }
        });
    }


    @Override
    public int getItemCount() {
        Log.d("ITEMCOUNT>>",comm.size()+"");
        return  comm.size();
    }



    // stores and recycles views as they are scrolled off screen
    public class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        TextView response;
        ImageView image;



        CommentViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.comment);
            image = itemView.findViewById(R.id.thumbImage);
            response = itemView.findViewById(R.id.resp_comment);
            itemView.setOnClickListener(this);
        }



        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // allows clicks events to be caught
   public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
