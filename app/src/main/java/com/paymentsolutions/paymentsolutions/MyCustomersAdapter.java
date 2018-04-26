package com.paymentsolutions.paymentsolutions;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyCustomersAdapter extends RecyclerView.Adapter<MyCustomersAdapter.CustomerHolder> {


    private Context mContext;

    private ArrayList<CustomerModel> items;

    private OnItemClick onItemClick;

    public MyCustomersAdapter(Context mContext, ArrayList<CustomerModel> items, OnItemClick onItemClick) {
        this.mContext = mContext;
        this.items = items;
        this.onItemClick = onItemClick;
    }

    @Override
    public CustomerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.customer_item, parent,false);
        return new CustomerHolder(view);
    }


    @Override
    public void onBindViewHolder(CustomerHolder holder, int position) {
        holder.customerName.setText(items.get(position).getName());
        holder.email.setText(items.get(position).getEmail());
        holder.phone.setText(items.get(position).getPhone());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class CustomerHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.customer_name)
        TextView customerName;
        @BindView(R.id.phone)
        TextView phone;
        @BindView(R.id.email)
        TextView email;

        public CustomerHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            onItemClick.setOnItemClick(position);
        }
    }

    public interface OnItemClick {
        void setOnItemClick(int position);
    }

}
