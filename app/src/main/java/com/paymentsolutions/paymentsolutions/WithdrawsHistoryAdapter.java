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

public class WithdrawsHistoryAdapter extends RecyclerView.Adapter<WithdrawsHistoryAdapter.WithdrawHistoryHolder> {

    private Context mContext;

    private ArrayList<WithdrawHistoryModel> items;

    private OnItemClick onItemClick;

    public WithdrawsHistoryAdapter(Context mContext, ArrayList<WithdrawHistoryModel> items, OnItemClick onItemClick) {
        this.mContext = mContext;
        this.items = items;
        this.onItemClick = onItemClick;
    }

    @Override
    public WithdrawHistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.withdraw_history_item, parent,false);
        return new WithdrawHistoryHolder(view);
    }


    @Override
    public void onBindViewHolder(WithdrawHistoryHolder holder, int position) {
        holder.amount.setText(items.get(position).getAmount());
        holder.status.setText(items.get(position).getStatus());
        holder.date.setText(items.get(position).getDate());
        holder.fawry.setText(items.get(position).getFawry());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class WithdrawHistoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.amount)
        TextView amount;
        @BindView(R.id.status)
        TextView status;
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.fawry)
        TextView fawry;

        public WithdrawHistoryHolder(View itemView) {
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
