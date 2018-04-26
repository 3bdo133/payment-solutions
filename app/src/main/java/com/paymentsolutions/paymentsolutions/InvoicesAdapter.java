package com.paymentsolutions.paymentsolutions;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InvoicesAdapter extends RecyclerView.Adapter<InvoicesAdapter.InvoiceHolder> {

    private Context mContext;

    private ArrayList<Invoice> items;

    private OnItemClick onItemClick;

    private OnPriceChanged onPriceChanged;

    private OnRemoveClicked onRemoveClicked;

    private int mType;


    public InvoicesAdapter(Context mContext, ArrayList<Invoice> items, OnItemClick onItemClick, OnPriceChanged onPriceChanged, OnRemoveClicked onRemoveClicked, int mType) {
        this.mContext = mContext;
        this.items = items;
        this.onItemClick = onItemClick;
        this.onPriceChanged = onPriceChanged;
        this.onRemoveClicked = onRemoveClicked;
        this.mType = mType;
    }

    @Override
    public InvoiceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.invoice_item, parent, false);
        return new InvoiceHolder(view);
    }

    @Override
    public void onBindViewHolder(InvoiceHolder holder, int position) {
        if (mType == 1) {
            holder.image.setVisibility(View.GONE);
            holder.name.setText(items.get(position).getDescription());
            holder.price.setText(items.get(position).getPrice());
            holder.quantity.setText(items.get(position).getQuantity());
            holder.name.setEnabled(false);
            holder.quantity.setEnabled(false);
            holder.price.setEnabled(false);
            Log.i("true","true");
        } else {
            holder.price.setText("");
            holder.quantity.setText("");
            holder.name.setText("");
            holder.image.setVisibility(View.VISIBLE);
            holder.name.setEnabled(true);
            holder.quantity.setEnabled(true);
            holder.price.setEnabled(true);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class InvoiceHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.name)
        EditText name;
        @BindView(R.id.quantity_number)
        EditText quantity;
        @BindView(R.id.product_image)
        ImageView image;
        @BindView(R.id.price_num)
        EditText price;

        public InvoiceHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    items.get(getAdapterPosition()).setDescription(s.toString());
                }
            });
            price.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    items.get(getAdapterPosition()).setPrice(s.toString());
                    onPriceChanged.setOnPriceChange(s.toString());
                }
            });
            quantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    items.get(getAdapterPosition()).setQuantity(s.toString());
                    onPriceChanged.setOnPriceChange(s.toString());
                }
            });
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRemoveClicked.setOnRemove(getAdapterPosition());
                }
            });
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

    public interface OnPriceChanged {
        void setOnPriceChange(String price);
    }

    public interface OnRemoveClicked {
        void setOnRemove(int position);
    }

    public void addItem(Invoice invoice) {
        items.add(invoice);
    }


}
