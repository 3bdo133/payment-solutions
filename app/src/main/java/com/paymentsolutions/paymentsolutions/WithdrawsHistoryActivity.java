package com.paymentsolutions.paymentsolutions;

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

public class WithdrawsHistoryActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.mobiles)
    RecyclerView mobiles;
    @BindView(R.id.parent_layout)
    View mParentLayout;
    @BindView(R.id.progress_indicator)
    ProgressBar mProgressBar;

    String balanceUpdated;


    ArrayList<WithdrawHistoryModel> withdrawHistoryModels;

    RequestQueue mRequestQueue;
    StringRequest mStringRequest;

    public final String TAG = "WithdrawsHistoryActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraws_history);

        ButterKnife.bind(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-RobotoRegular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );


        setSupportActionBar(toolbar);
        setTitle(getString(R.string.withdraws_history));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);

        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.titleview, null);

        ((TextView) v.findViewById(R.id.title)).setText(this.getTitle());

        this.getSupportActionBar().setCustomView(v);

        withdrawHistoryModels = new ArrayList<>();

        final WithdrawsHistoryAdapter adapter = new WithdrawsHistoryAdapter(this, withdrawHistoryModels, new WithdrawsHistoryAdapter.OnItemClick() {
            @Override
            public void setOnItemClick(int position) {

            }
        });

        mobiles.setAdapter(adapter);


        mobiles.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));


        mRequestQueue = Volley.newRequestQueue(this);

        mStringRequest = new StringRequest(Request.Method.POST, ContractClass.MY_WITHDRAWS_URL, new Response.Listener<String>() {
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
                            JSONArray jsonArray = jsonObject.getJSONArray("withdraws");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject info = jsonArray.getJSONObject(i);
                                JSONObject collect = info.getJSONObject("Collect");
                                String amount = collect.getString("amount");
                                String date = collect.getString("created");
                                String fawry = collect.getString("key_string");
                                String status = collect.getString("status");
                                withdrawHistoryModels.add(new WithdrawHistoryModel(amount, date, status, fawry));
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
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> stringStringMap = new HashMap<>();
                stringStringMap.put("id", getIntent().getStringExtra("id"));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.withdraw_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.withdraw) {
            startActivityForResult(new Intent(this, WithdrawActivity.class).putExtra("id", getIntent().getStringExtra("id")), 1);
            return true;
        } else if (item.getItemId() == android.R.id.home){
            if (balanceUpdated != null || !TextUtils.isEmpty(balanceUpdated)){
            Intent resultIntent = new Intent();
            resultIntent.putExtra("case", "done");
            resultIntent.putExtra("new_balance",balanceUpdated);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
            super.onBackPressed();}
            else{
                super.onBackPressed();
            }
        }
        return false;
    }


    private void showSnackBarMessage(String message) {

        if (findViewById(android.R.id.content) != null) {

            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (data != null) {
                if (data.getStringExtra("case").equals("done")) {
                    showSnackBarMessage(getString(R.string.withdraw_successful));
                    balanceUpdated = data.getStringExtra("new_balance");
                    Log.i("balance",balanceUpdated);
                }
            } else {

            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(TAG);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mRequestQueue.add(mStringRequest);
    }

    @Override
    public void onBackPressed() {
        if (balanceUpdated != null || !TextUtils.isEmpty(balanceUpdated)){
        Intent resultIntent = new Intent();
        resultIntent.putExtra("case", "done");
        resultIntent.putExtra("new_balance",balanceUpdated);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
        super.onBackPressed();}else{
            super.onBackPressed();
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
