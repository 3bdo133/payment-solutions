package com.paymentsolutions.paymentsolutions;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Abdelrahman Hesham on 3/8/2018.
 */

public class MobileAdapter extends RecyclerView.Adapter<MobileAdapter.MobileHolder> {


    private ArrayList<Mobile> items;

    private OnItemClick onItemClick;

    public MobileAdapter(ArrayList<Mobile> items, OnItemClick onItemClick) {
        this.items = items;
        this.onItemClick = onItemClick;
    }

    @Override
    public MobileAdapter.MobileHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mobile_item, null);
        MobileAdapter.MobileHolder holder = new MobileAdapter.MobileHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MobileAdapter.MobileHolder holder, int position) {
        holder.name.setText(items.get(position).getName());
        holder.price.setText(Html.fromHtml(items.get(position).getPrice()));
        if (!TextUtils.isEmpty(items.get(position).getImage())) {
            Picasso.with(holder.image.getContext()).load(items.get(position).getImage()).into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MobileHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.price)
        TextView price;
        @BindView(R.id.product_image)
        ImageView image;

        public MobileHolder(View itemView) {
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
