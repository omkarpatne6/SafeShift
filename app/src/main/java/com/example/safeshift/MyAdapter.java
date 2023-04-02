package com.example.safeshift;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    Context context;
    ArrayList<PreviousEmployeeOrders> list;
    OnItemClickListener onItemClickListener;

    public MyAdapter(Context context, ArrayList<PreviousEmployeeOrders> list) {
        this.context = context;
        this.list = list;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.employee_order_items, parent, false);
        return new MyViewHolder(v, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        PreviousEmployeeOrders orders = list.get(position);
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
        OnItemClickListener onItemClickListener;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
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

