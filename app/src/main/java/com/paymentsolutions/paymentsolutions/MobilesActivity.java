package com.paymentsolutions.paymentsolutions;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

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
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MobilesActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.mobiles)
    RecyclerView mobiles;
    @BindView(R.id.progress_indicator)
    ProgressBar mProgressIndicator;
    @BindView(R.id.parent_layout)
    View mParentLayout;


    RequestQueue queue;
    StringRequest categoryRequest;
    StringRequest vendorRequest;


    String categoryId;
    String userId;

    public static final String TAG = "MyTag";


    ArrayList<Mobile> mobilesItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobiles);

        ButterKnife.bind(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-RobotoRegular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );


        setSupportActionBar(toolbar);
        setTitle(getIntent().getStringExtra("title").replaceAll("<b>","").replaceAll("</b>",""));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);

        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.titleview, null);

        ((TextView) v.findViewById(R.id.title)).setText(this.getTitle());

        this.getSupportActionBar().setCustomView(v);

        mobilesItems = new ArrayList<>();


        final MobileAdapter mobileAdapter = new MobileAdapter(mobilesItems, new MobileAdapter.OnItemClick() {
            @Override
            public void setOnItemClick(int position) {
                startActivity(new Intent(MobilesActivity.this,ProductActivity.class).putExtra("id",mobilesItems.get(position).getId()).putExtra("name",mobilesItems.get(position).getName()));
            }
        });

        int numberOfColumns = Utility.calculateNoOfColumns(getApplicationContext());

        mobiles.setLayoutManager(new GridLayoutManager(MobilesActivity.this, numberOfColumns));
        mobiles.setAdapter(mobileAdapter);


        queue = Volley.newRequestQueue(this);


        Intent intent = getIntent();
        if (intent.hasExtra("category_id")) {
            categoryId = intent.getStringExtra("category_id");
            String url = Uri.parse(ContractClass.GET_PRODUCTS_URL).buildUpon()
                    .appendQueryParameter("category_id", categoryId).toString();
            categoryRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("response", response);
                            if (response != null && isJSONValid(response)) {
                                if (response.contains("false")) {
                                    finish();
                                    showSnackBarMessage(getString(R.string.error));
                                } else {
                                    mParentLayout.setVisibility(View.VISIBLE);
                                    mProgressIndicator.setVisibility(View.GONE);
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                                        JSONArray jsonArray = jsonObject1.getJSONArray("product");
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                            String id = jsonObject2.getString("id");
                                            String name = jsonObject2.getString("name");
                                            String price = jsonObject2.getString("price");
                                            String image = jsonObject2.getString("image");
                                            mobilesItems.add(new Mobile(id,name, price + " EGP",image));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                finish();
                                showSnackBarMessage(getString(R.string.error_try_again));
                            }
                            mobileAdapter.notifyDataSetChanged();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            categoryRequest.setTag(TAG);

            mParentLayout.setVisibility(View.GONE);
            mProgressIndicator.setVisibility(View.VISIBLE);

            queue.add(categoryRequest);

        } else {
            userId = intent.getStringExtra("user_id");
            String url = Uri.parse(ContractClass.GET_PRODUCTS_URL).buildUpon()
                    .appendQueryParameter("user_id", userId).toString();
            vendorRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("response", response);
                            if (response != null && isJSONValid(response) && !TextUtils.isEmpty(response)) {
                                if (response.contains("false")) {
                                    finish();
                                    showSnackBarMessage(getString(R.string.error));
                                } else {
                                    mParentLayout.setVisibility(View.VISIBLE);
                                    mProgressIndicator.setVisibility(View.GONE);
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                                        JSONArray jsonArray = jsonObject1.getJSONArray("product");
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                            String id = jsonObject2.getString("id");
                                            String name = jsonObject2.getString("name");
                                            String price = jsonObject2.getString("price");
                                            String image = jsonObject2.getString("image");
                                            mobilesItems.add(new Mobile(id,name, price + " EGP",image));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                finish();
                                showSnackBarMessage(getString(R.string.error_try_again));
                            }
                            mobileAdapter.notifyDataSetChanged();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            vendorRequest.setTag(TAG);

            mParentLayout.setVisibility(View.GONE);
            mProgressIndicator.setVisibility(View.VISIBLE);

            queue.add(vendorRequest);
        }

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
