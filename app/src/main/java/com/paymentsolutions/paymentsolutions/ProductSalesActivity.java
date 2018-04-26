package com.paymentsolutions.paymentsolutions;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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

public class ProductSalesActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.items)
    RecyclerView mobiles;
    @BindView(R.id.parent_layout)
    View mParentLayout;
    @BindView(R.id.progress_indicator)
    ProgressBar mProgressBar;


    ArrayList<ProductSaleModel> productSaleModels;


    RequestQueue mRequestQueue;
    StringRequest mStringRequest;

    public final String TAG = "ProductSalesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_sales);

        ButterKnife.bind(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-RobotoRegular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );


        setSupportActionBar(toolbar);
        setTitle(getString(R.string.product_sales));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);

        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.titleview, null);

        ((TextView) v.findViewById(R.id.title)).setText(this.getTitle());

        this.getSupportActionBar().setCustomView(v);

        productSaleModels = new ArrayList<>();

        final ProductSalesAdapter adapter = new ProductSalesAdapter(this,productSaleModels, new ProductSalesAdapter.OnItemClick() {
            @Override
            public void setOnItemClick(int position) {

            }
        });

        mobiles.setAdapter(adapter);

        mobiles.setFocusable(false);
        mobiles.setNestedScrollingEnabled(false);

        mobiles.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mRequestQueue = Volley.newRequestQueue(this);

        mStringRequest = new StringRequest(Request.Method.POST, ContractClass.PRODUCT_SALES_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("response", response);
                mParentLayout.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                if (response != null && isJSONValid(response)) {
                    if (response.contains("false")) {
                        finish();
                        showSnackBarMessage(getString(R.string.error));
                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("products");
                            for (int i = 0 ; i<jsonArray.length();i++){
                                JSONObject allJson = jsonArray.getJSONObject(i);
                                JSONObject info = allJson.getJSONObject("0");
                                String totalQuantity = info.getString("total_quantity");
                                String totalPrice = info.getString("total_price");
                                JSONObject product = allJson.getJSONObject("Product");
                                String title = product.getString("title");
                                productSaleModels.add(new ProductSaleModel(title,totalQuantity,totalPrice));
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                finish();
                showSnackBarMessage(getString(R.string.error));
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> stringStringMap = new HashMap<>();
                stringStringMap.put("id",getIntent().getStringExtra("id"));
                return stringStringMap;
            }
        };

        mParentLayout.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mRequestQueue.add(mStringRequest);

        mStringRequest.setTag(TAG);

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

    private void showSnackBarMessage(String message) {

        if (findViewById(android.R.id.content) != null) {

            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mRequestQueue != null){
            mRequestQueue.cancelAll(TAG);
        }
    }


}
