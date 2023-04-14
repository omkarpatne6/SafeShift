package com.example.safeshift;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyUserAdapter extends RecyclerView.Adapter<MyUserAdapter.MyViewHolder> {

    Context context;
    ArrayList<PreviousUserOrders> list;
    MyUserAdapter.OnItemClickListener onItemClickListener;

    public MyUserAdapter(Context context, ArrayList<PreviousUserOrders> list) {
        this.context = context;
        this.list = list;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(MyUserAdapter.OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    @NonNull
    @Override
    public MyUserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.user_order_items, parent, false);
        return new MyViewHolder(v, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyUserAdapter.MyViewHolder holder, int position) {
        PreviousUserOrders orders = list.get(position);
        holder.order_name.setText(orders.getOrder_name());
        holder.pickup_address.setText(orders.getPickup_address());
        holder.destination_address.setText(orders.getDestination_address());
        holder.pickup_date.setText(orders.getPickup_date());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView order_name, pickup_address, destination_address, pickup_date;
        MyUserAdapter.OnItemClickListener onItemClickListener;

        public MyViewHolder(@NonNull View itemView, MyUserAdapter.OnItemClickListener onItemClickListener) {
            super(itemView);

            this.onItemClickListener = onItemClickListener;
            itemView.setOnClickListener(this);

            order_name = itemView.findViewById(R.id.order_name);
            pickup_address = itemView.findViewById(R.id.pickup_address);
            destination_address = itemView.findViewById(R.id.destination_address);
            pickup_date = itemView.findViewById(R.id.pickup_date);
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(position);
                }
            }
        }
    }
}
