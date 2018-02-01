package com.paymentsolutions.paymentsolutions;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Abdelrahman Hesham on 1/22/2018.
 */

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuHolder> {

    private ArrayList<MenuItem> items;

    private OnItemClick onItemClick;

    public MenuAdapter(ArrayList<MenuItem> items, OnItemClick onItemClick) {
        this.items = items;
        this.onItemClick = onItemClick;
    }

    @Override
    public MenuHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.menu_item, null);
        MenuHolder holder = new MenuHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MenuHolder holder, int position) {
        holder.imageView.setImageResource(items.get(position).getImage());
        holder.textView.setText(Html.fromHtml(items.get(position).getTitle()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MenuHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.image)ImageView imageView;
        @BindView(R.id.title)TextView textView;

        public MenuHolder(View itemView) {
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
