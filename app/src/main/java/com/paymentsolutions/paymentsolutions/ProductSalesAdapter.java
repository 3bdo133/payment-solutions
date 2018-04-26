package com.paymentsolutions.paymentsolutions;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductSalesAdapter extends RecyclerView.Adapter<ProductSalesAdapter.ProductSaleHolder> {

    private Context mContext;

    private ArrayList<ProductSaleModel> items;

    private OnItemClick onItemClick;

    public ProductSalesAdapter(Context mContext, ArrayList<ProductSaleModel> items, OnItemClick onItemClick) {
        this.mContext = mContext;
        this.items = items;
        this.onItemClick = onItemClick;
    }

    @Override
    public ProductSaleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_sales_item, parent,false);
        return new ProductSaleHolder(view);
    }


    @Override
    public void onBindViewHolder(ProductSaleHolder holder, int position) {
        holder.productName.setText(items.get(position).getProductName());
        holder.quantity.setText(items.get(position).getProductQuantity());
        holder.total.setText(items.get(position).getProductTotal());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ProductSaleHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.product_name)
        TextView productName;
        @BindView(R.id.quantity)
        TextView quantity;
        @BindView(R.id.total)
        TextView total;

        public ProductSaleHolder(View itemView) {
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
