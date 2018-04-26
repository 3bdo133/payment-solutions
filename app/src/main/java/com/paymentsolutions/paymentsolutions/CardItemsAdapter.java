package com.paymentsolutions.paymentsolutions;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CardItemsAdapter extends RecyclerView.Adapter<CardItemsAdapter.CardHolder> {


    private ArrayList<CardItem> items;

    private OnItemClick onItemClick;

    public CardItemsAdapter(ArrayList<CardItem> items, OnItemClick onItemClick) {
        this.items = items;
        this.onItemClick = onItemClick;
    }

    @Override
    public CardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_item, null);
        CardHolder holder = new CardHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(CardHolder holder, int position) {
        holder.mProductName.setText(items.get(position).getProductName());
        holder.mProductPrice.setText(items.get(position).getProductPrice());
        holder.mProductQuantity.setText(items.get(position).getProductQuantity());
        holder.mProductImage.setImageResource(items.get(position).getProductImage());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class CardHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.product_name)TextView mProductName;
        @BindView(R.id.product_price)TextView mProductPrice;
        @BindView(R.id.product_image) ImageView mProductImage;
        @BindView(R.id.product_quantity) TextView mProductQuantity;

        public CardHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
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
