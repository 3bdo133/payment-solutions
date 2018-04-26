package com.paymentsolutions.paymentsolutions;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InvoicesHistoryAdapter extends RecyclerView.Adapter<InvoicesHistoryAdapter.InvoiceHistoryHolder> {

    private Context mContext;

    private ArrayList<InvoiceHistoryModel> items;

    private OnItemClick onItemClick;

    public InvoicesHistoryAdapter(Context mContext, ArrayList<InvoiceHistoryModel> items, OnItemClick onItemClick) {
        this.mContext = mContext;
        this.items = items;
        this.onItemClick = onItemClick;
    }

    @Override
    public InvoiceHistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.invoices_history_item, parent,false);
        return new InvoiceHistoryHolder(view);
    }

    @Override
    public void onBindViewHolder(InvoiceHistoryHolder holder, int position) {
        holder.billTo.setText(items.get(position).getBillTo());
        holder.invoiceDate.setText(items.get(position).getInvoiceDate());
        holder.balance.setText(items.get(position).getBalance());
        holder.invoiceNumber.setText(items.get(position).getId());
        if (items.get(position).getStatus().equals("Shipped")){
            holder.status.setTextColor(ContextCompat.getColor(mContext,R.color.colorPrimaryDark));
            holder.status.setText(items.get(position).getStatus());
        } else if (items.get(position).getStatus().equals("Declined")){
            holder.status.setText(items.get(position).getStatus());
            holder.status.setTextColor(Color.RED);
        } else if (items.get(position).getStatus().equals("Delivered")){
            holder.status.setText(items.get(position).getStatus());
            holder.status.setTextColor(Color.GREEN);
        } else {
            holder.status.setText(items.get(position).getStatus());
            holder.status.setTextColor(Color.GRAY);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class InvoiceHistoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.bill_to)
        TextView billTo;
        @BindView(R.id.invoice_date)
        TextView invoiceDate;
        @BindView(R.id.ivoice_balance)
        TextView balance;
        @BindView(R.id.invoice_status)
        TextView status;
        @BindView(R.id.invoice_number)
        TextView invoiceNumber;

        public InvoiceHistoryHolder(View itemView) {
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
