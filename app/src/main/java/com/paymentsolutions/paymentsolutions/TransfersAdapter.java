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

public class TransfersAdapter extends RecyclerView.Adapter<TransfersAdapter.TransferHolder> {


    private Context mContext;

    private ArrayList<TransferModel> items;

    private OnItemClick onItemClick;

    public TransfersAdapter(Context mContext, ArrayList<TransferModel> items, OnItemClick onItemClick) {
        this.mContext = mContext;
        this.items = items;
        this.onItemClick = onItemClick;
    }


    @Override
    public TransferHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transfer_item, parent,false);
        return new TransferHolder(view);
    }


    @Override
    public void onBindViewHolder(TransferHolder holder, int position) {
        holder.customerName.setText(items.get(position).getName());
        holder.amount.setText(items.get(position).getAmount());
        holder.status.setText(items.get(position).getStatus());
        holder.date.setText(items.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class TransferHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.customer_name)
        TextView customerName;
        @BindView(R.id.amount)
        TextView amount;
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.status)
        TextView status;

        public TransferHolder(View itemView) {
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
