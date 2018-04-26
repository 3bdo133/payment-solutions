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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
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

public class CardActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.mobiles)
    RecyclerView mobiles;
    @BindView(R.id.progress_indicator)
    ProgressBar mProgressIndicator;
    @BindView(R.id.parent_layout)
    View mParentLayout;
    @BindView(R.id.scroll_view)
    ScrollView scrollView;

    RequestQueue queue;
    StringRequest stringRequest;
    String id;

    public static final String TAG = "MyTag";


    ArrayList<CardItem> cardItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        ButterKnife.bind(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-RobotoRegular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        setSupportActionBar(toolbar);
        setTitle(getString(R.string.card));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);

        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.titleview, null);

        ((TextView) v.findViewById(R.id.title)).setText(this.getTitle());

        this.getSupportActionBar().setCustomView(v);

        id = getIntent().getStringExtra("id");

        cardItems = new ArrayList<>();


        final CardItemsAdapter cardItemsAdapter = new CardItemsAdapter(cardItems, new CardItemsAdapter.OnItemClick() {
            @Override
            public void setOnItemClick(int position) {

            }
        });

        mobiles.setAdapter(cardItemsAdapter);
        mobiles.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mobiles.setFocusable(false);

        mobiles.setNestedScrollingEnabled(false);


        queue = Volley.newRequestQueue(this);

        stringRequest = new StringRequest(Request.Method.POST, ContractClass.VIEW_CART_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null && !TextUtils.isEmpty(response)&& isJSONValid(response)){
                    Log.i("cart", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                        JSONArray jsonArray = jsonObject1.getJSONArray("cart");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                            String id = jsonObject2.getString("id");
                            String name = jsonObject2.getString("name");
                            String price = jsonObject2.getString("price");
                            String quantity = jsonObject2.getString("quantity");
                            cardItems.add(new CardItem(name, price, quantity, R.drawable.visa));
                        }
                        mParentLayout.setVisibility(View.VISIBLE);
                        mProgressIndicator.setVisibility(View.INVISIBLE);
                        cardItemsAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        showSnackBarMessage(getString(R.string.error));
                        finish();
                        e.printStackTrace();
                    }
                } else {
                    showSnackBarMessage(getString(R.string.error));
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> stringStringHashMap = new HashMap<>();
                stringStringHashMap.put("id", id);
                return stringStringHashMap;
            }
        };


        mParentLayout.setVisibility(View.INVISIBLE);
        mProgressIndicator.setVisibility(View.VISIBLE);
        queue.add(stringRequest);
        stringRequest.setTag(TAG);

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

}
