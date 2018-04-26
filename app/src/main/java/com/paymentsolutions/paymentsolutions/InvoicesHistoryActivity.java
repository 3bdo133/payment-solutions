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
import android.view.Menu;
import android.view.MenuItem;
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

public class InvoicesHistoryActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recent_parent)
    View mRecentParent;
    @BindView(R.id.older_parent)
    View mOlderParent;
    @BindView(R.id.progress_indicator)
    ProgressBar mProgressBar;
    @BindView(R.id.older_recycler)
    RecyclerView mOlderRecycler;
    @BindView(R.id.recent_recycler)
    RecyclerView mRecentRecycler;

    ArrayList<InvoiceHistoryModel> invoiceHistoryModels;

    RequestQueue queue;
    StringRequest stringRequest;

    String mId;

    String name;
    String idGlobal;
    String email;
    String role;

    private final String TAG = "InvoicesHistoryActivity";

    public final static int REQUEST_CODE_4 = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoices_history);
        ButterKnife.bind(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/OpenSans-Bold.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        toolbar.setTitle(getString(R.string.invoices_history));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.invoices_history));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);

        name = getIntent().getStringExtra("name");
        idGlobal = getIntent().getStringExtra("id");
        email = getIntent().getStringExtra("email");
        role = getIntent().getStringExtra("role");

        LayoutInflater inflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.titleview, null);

        ((TextView) v.findViewById(R.id.title)).setText(getString(R.string.invoices_history));

        this.getSupportActionBar().setCustomView(v);


        invoiceHistoryModels = new ArrayList<>();

        final InvoicesHistoryAdapter adapter = new InvoicesHistoryAdapter(this, invoiceHistoryModels, new InvoicesHistoryAdapter.OnItemClick() {
            @Override
            public void setOnItemClick(int position) {
                startActivity(new Intent(InvoicesHistoryActivity.this, ShowInvoiceActivity.class).putExtra("id", invoiceHistoryModels.get(position).getId()).putExtra("role",role));
            }
        });

        mId = getIntent().getStringExtra("id");

        mOlderRecycler.setAdapter(adapter);

        mOlderRecycler.setFocusable(false);
        mOlderRecycler.setNestedScrollingEnabled(false);

        mOlderRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));


        mRecentRecycler.setAdapter(adapter);

        mRecentRecycler.setFocusable(false);
        mRecentRecycler.setNestedScrollingEnabled(false);

        mRecentRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));


        queue = Volley.newRequestQueue(this);

        stringRequest = new StringRequest(Request.Method.POST, ContractClass.GET_INVOICES_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Result", response);
                        if (response != null && isJSONValid(response) && !TextUtils.isEmpty(response)) {
                            if (response.contains("false")) {
                                mRecentParent.setVisibility(View.VISIBLE);
                                mOlderParent.setVisibility(View.VISIBLE);
                                mProgressBar.setVisibility(View.INVISIBLE);
                                showSnackBarMessage(getString(R.string.error_view_invoices_history));
                            } else {
                                mRecentParent.setVisibility(View.VISIBLE);
                                mOlderParent.setVisibility(View.VISIBLE);
                                mProgressBar.setVisibility(View.INVISIBLE);
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                                    JSONArray jsonArray = jsonObject1.getJSONArray("invoices");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        String id = object.getString("id");
                                        String date = object.getString("date");
                                        String name = object.getString("vendor_name");
                                        String price = object.getString("price");
                                        String status = object.getString("status");
                                        invoiceHistoryModels.add(new InvoiceHistoryModel(name, date, price, status, id));
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
                showSnackBarMessage(getString(R.string.error_try_again));
                finish();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> stringStringMap = new HashMap<>();
                stringStringMap.put("id", mId);
                return stringStringMap;
            }
        };


        stringRequest.setTag(TAG);
        mRecentParent.setVisibility(View.INVISIBLE);
        mOlderParent.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        queue.add(stringRequest);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_invoices_history, menu);
        menu.getItem(0).setVisible(false);
        if (Integer.parseInt(role) == 0 || Integer.parseInt(role) == 2) {
            menu.getItem(0).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_invoice) {
            startActivityForResult(new Intent(this, InvoicesActivity.class).putExtra("name", name).putExtra("id", idGlobal).putExtra("email", email), REQUEST_CODE_4);
            return true;
        }
        return false;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        queue.add(stringRequest);
    }
}
