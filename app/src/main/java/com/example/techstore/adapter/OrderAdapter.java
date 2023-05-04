package com.example.techstore.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techstore.R;
import com.example.techstore.models.Orders;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Orders> ordersList = new ArrayList<>();

    public void setOrdersList(List<Orders> ordersList) {
        this.ordersList = ordersList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item_layout, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Orders order = ordersList.get(position);
        // Đưa các giá trị của đơn hàng vào ViewHolder tương ứng
        holder.orderIdTextView.setText(String.valueOf(order.getOrderId()));
        holder.dateTextView.setText("Ngày Đặt: "+order.getDate());
        holder.orderStatusTextView.setText("Trạng thái đơn hàng: "+order.getOrderStatus());
        holder.totalPriceTextView.setText("Tổng tiền: "+order.getTotal_price()+"$");
        holder.paymentMethodTextView.setText("Hình thức thanh toán: "+order.getPaymentMethod());
        // Hiển thị danh sách các sản phẩm trong đơn hàng
        holder.orderItemsRecyclerView.setAdapter(new OrderItemsAdapter(order.getOrderItems()));
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {

        private TextView orderIdTextView;
        private TextView dateTextView;
        private TextView orderStatusTextView;
        private TextView totalPriceTextView;
        private TextView paymentMethodTextView;
        private RecyclerView orderItemsRecyclerView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.order_id_text_view);
            dateTextView = itemView.findViewById(R.id.date_text_view);
            orderStatusTextView = itemView.findViewById(R.id.order_status_text_view);
            totalPriceTextView = itemView.findViewById(R.id.total_price_text_view);
            paymentMethodTextView = itemView.findViewById(R.id.payment_method_text_view);
            orderItemsRecyclerView = itemView.findViewById(R.id.order_items_recycler_view);
            orderItemsRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        }
    }

}
