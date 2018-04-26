package com.paymentsolutions.paymentsolutions;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.transition.Slide;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProductActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.progress_indicator)
    ProgressBar mProgressIndicator;
    @BindView(R.id.parent)
    View mParentLayout;
    @BindView(R.id.image)
    ImageView imageView;
    @BindView(R.id.product_name)
    TextView mProdTextView;
    @BindView(R.id.price)
    TextView mPriceTextView;
    @BindView(R.id.vendor_name)
    TextView mVendorName;
    @BindView(R.id.submit)
    Button mAddToCart;
    RequestQueue queue;
    String title;
    String productId;
    String id;
    public static final String TAG = "MyTag";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);


        ButterKnife.bind(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-RobotoRegular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        id = getIntent().getStringExtra("id");

        queue = Volley.newRequestQueue(this);


        String url = Uri.parse(ContractClass.GET_PRODUCT_URL).buildUpon()
                .appendQueryParameter("id", id).toString();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("response", response);
                        if (response != null && isJSONValid(response) && !TextUtils.isEmpty(response)) {
                            if (response.contains("false")) {
                                finish();
                                showSnackBarMessage(getString(R.string.error));
                            } else {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                                    JSONObject jsonObject2 = jsonObject1.getJSONObject("product");
                                    productId = jsonObject2.getString("id");
                                    title = jsonObject2.getString("title");
                                    String price = jsonObject2.getString("price");
                                    String image = jsonObject2.getString("image");
                                    String vendorName = jsonObject2.getString("vendor_name");
                                    mProdTextView.setText(title);
                                    mPriceTextView.setText(price + " EGP");
                                    Picasso.with(ProductActivity.this).load("https://www.fawaterk.com/fawaterk/product_img/" + image).into(imageView);
                                    mVendorName.setText(vendorName);
                                    mProgressIndicator.setVisibility(View.GONE);
                                    mParentLayout.setVisibility(View.VISIBLE);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            finish();
                            showSnackBarMessage(getString(R.string.error_try_again));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        stringRequest.setTag(TAG);

        mParentLayout.setVisibility(View.GONE);
        mProgressIndicator.setVisibility(View.VISIBLE);
        queue.add(stringRequest);


        final StringRequest addToCartRequest = new StringRequest(Request.Method.POST, ContractClass.ADD_CHECKOUT_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null && isJSONValid(response) && !TextUtils.isEmpty(response)) {
                    Log.i("Add to cart", response);
                    mProgressIndicator.setVisibility(View.GONE);
                    mParentLayout.setVisibility(View.VISIBLE);
                    if (response.contains("true")) {
                        showSnackBarMessage(getString(R.string.added));
                    } else {
                        showSnackBarMessage(getString(R.string.add_error));
                    }
                } else {
                    showSnackBarMessage(getString(R.string.error));
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showSnackBarMessage("Error in add to cart");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> stringStringHashMap = new HashMap<>();
                stringStringHashMap.put("id", id);
                stringStringHashMap.put("product_id", productId);
                return stringStringHashMap;
            }
        };

        addToCartRequest.setTag(TAG);

        mAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mParentLayout.setVisibility(View.GONE);
                mProgressIndicator.setVisibility(View.VISIBLE);
                queue.add(addToCartRequest);
            }
        });

        getSupportActionBar().setTitle(getIntent().getStringExtra("name"));

        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);

        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.titleview, null);

        ((TextView) v.findViewById(R.id.title)).setText(getIntent().getStringExtra("name"));

        this.getSupportActionBar().setCustomView(v);

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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


    private void showSnackBarMessage(String message) {

        if (findViewById(android.R.id.content) != null) {

            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String lang = preferences.getString("lang", "error");
        if (lang.equals("error")) {
            if (Locale.getDefault().getLanguage().equals("ar"))
                setLocale("ar");
            else
                setLocale("en");
        } else if (lang.equals("en")) {
            setLocale("en");
        } else {
            setLocale("ar");
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


}
