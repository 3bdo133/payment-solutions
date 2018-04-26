package com.paymentsolutions.paymentsolutions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ShowInvoiceActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.invoice_id)
    TextView mInvoiceIdText;
    @BindView(R.id.invoices)
    RecyclerView mInvoicesRecyclerView;
    @BindView(R.id.shop)
    TextView mShopName;
    @BindView(R.id.email_cus)
    TextView mEmail;
    @BindView(R.id.shop_vendor)
    TextView mVendorName;
    @BindView(R.id.email_cus_vendor)
    TextView mVendorEmail;
    @BindView(R.id.invoice_date)
    TextView mInvoiceDate;
    @BindView(R.id.progress_indicator)
    ProgressBar progressBar;
    @BindView(R.id.parent_layout)
    View mParentLayout;
    @BindView(R.id.sub_total_num)
    TextView mSubTotal;
    @BindView(R.id.total_num)
    TextView mTotal;
    @BindView(R.id.balance_num)
    TextView mBalance;
    @BindView(R.id.save_button)
    Button mPayButton;

    String mInvoiceId;
    String id;

    RequestQueue queue;
    StringRequest stringRequest;

    ArrayList<Invoice> invoices;
    InvoicesAdapter invoicesAdapter;

    private final String TAG = "ShowInvoiceActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_invoice);
        ButterKnife.bind(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/OpenSans-Bold.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        queue = Volley.newRequestQueue(this);

        mInvoiceId = getIntent().getStringExtra("id");


        if(getIntent().getStringExtra("role").equals("3")){
            mPayButton.setVisibility(View.VISIBLE);
        }

        toolbar.setTitle(getString(R.string.show_invoice));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.show_invoice));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);

        LayoutInflater inflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.titleview, null);

        ((TextView) v.findViewById(R.id.title)).setText(getString(R.string.show_invoice));

        this.getSupportActionBar().setCustomView(v);

        id = getIntent().getStringExtra("id");

        invoices = new ArrayList<>();

        invoicesAdapter = new InvoicesAdapter(this, invoices, new InvoicesAdapter.OnItemClick() {
            @Override
            public void setOnItemClick(int position) {

            }
        }, new InvoicesAdapter.OnPriceChanged() {
            @Override
            public void setOnPriceChange(String price) {

            }
        }, new InvoicesAdapter.OnRemoveClicked() {
            @Override
            public void setOnRemove(int position) {

            }
        },1);

        mInvoicesRecyclerView.setAdapter(invoicesAdapter);
        mInvoicesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mInvoicesRecyclerView.setFocusable(false);

        mInvoicesRecyclerView.setNestedScrollingEnabled(false);

        stringRequest = new StringRequest(Request.Method.POST, ContractClass.GET_INVOICE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG,response);
                if (response != null && isJSONValid(response) && !TextUtils.isEmpty(response)) {
                    if (response.contains("true")) {
                        try {
                            progressBar.setVisibility(View.INVISIBLE);
                            mParentLayout.setVisibility(View.VISIBLE);
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject object = jsonObject.getJSONObject("result");
                            JSONArray array = object.getJSONArray("invoices");
                            JSONObject invoice = array.getJSONObject(0);
                            mEmail.setText(invoice.getString("customer_email"));
                            mShopName.setText(invoice.getString("customer_name"));
                            mVendorName.setText(invoice.getString("vendor_name"));
                            mShopName.setSelected(true);
                            mVendorName.setSelected(true);
                            mVendorEmail.setSelected(true);
                            mEmail.setSelected(true);
                            mVendorEmail.setText(invoice.getString("vendor_email"));
                            mInvoiceDate.setText(invoice.getString("date"));
                            mSubTotal.setText(invoice.getString("sub_total"));
                            mTotal.setText(invoice.getString("total"));
                            mBalance.setText(invoice.getString("total"));
                            mInvoiceIdText.setText(invoice.getString("id"));
                            JSONArray products = invoice.getJSONArray("products");
                            for (int i = 0 ;i<products.length();i++){
                                JSONObject product = products.getJSONObject(i);
                                String name = product.getString("name");
                                String price = product.getString("price");
                                String quantity = product.getString("quantity");
                                invoices.add(new Invoice(name,quantity,price,R.drawable.profile));
                            }
                            invoicesAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        mParentLayout.setVisibility(View.VISIBLE);
                        showSnackBarMessage(getString(R.string.error));
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String ,String> stringStringMap = new HashMap<>();
                stringStringMap.put("id",mInvoiceId);
                return stringStringMap;
            }
        };


        stringRequest.setTag(TAG);
        mParentLayout.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        queue.add(stringRequest);
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String lang = preferences.getString("lang", "error");
        switch (lang) {
            case "error":
                if (Locale.getDefault().getLanguage().equals("ar"))
                    setLocale("ar");
                else
                    setLocale("en");
                break;
            case "en":
                setLocale("en");
                break;
            default:
                setLocale("ar");
                break;
        }
    }

    public void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("lang", lang).apply();
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    private void showSnackBarMessage(String message) {

        if (findViewById(android.R.id.content) != null) {

            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (queue != null) {
            queue.cancelAll(TAG);
        }
    }

    public boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

}
