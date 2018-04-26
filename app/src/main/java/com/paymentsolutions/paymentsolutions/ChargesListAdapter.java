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

public class ChargesListAdapter extends RecyclerView.Adapter<ChargesListAdapter.ChargeHolder> {


    private Context mContext;

    private ArrayList<ChargeModel> items;

    private OnItemClick onItemClick;

    public ChargesListAdapter(Context mContext, ArrayList<ChargeModel> items, OnItemClick onItemClick) {
        this.mContext = mContext;
        this.items = items;
        this.onItemClick = onItemClick;
    }

    @Override
    public ChargeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.charges_item, parent,false);
        return new ChargeHolder(view);
    }


    @Override
    public void onBindViewHolder(ChargeHolder holder, int position) {
        holder.amount.setText(items.get(position).getAmount());
        holder.status.setText(items.get(position).getStatus());
        holder.date.setText(items.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ChargeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.amount)
        TextView amount;
        @BindView(R.id.status)
        TextView status;
        @BindView(R.id.date)
        TextView date;

        public ChargeHolder(View itemView) {
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
