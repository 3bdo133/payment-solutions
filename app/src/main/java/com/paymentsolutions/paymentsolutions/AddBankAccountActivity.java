package com.paymentsolutions.paymentsolutions;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.paymentsolutions.paymentsolutions.RegisterActivity.validateEmail;
import static com.paymentsolutions.paymentsolutions.RegisterActivity.validateFields;

public class AddBankAccountActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.parent_layout)
    View mParentLayout;
    @BindView(R.id.progress_indicator)
    ProgressBar mProgressBar;
    @BindView(R.id.submit)
    Button mSubmit;
    @BindView(R.id.f_name)
    EditText mFirstNameEditText;
    @BindView(R.id.id_number)
    EditText mIdNumberEditText;
    @BindView(R.id.bank_account)
    EditText mBankAccountEditText;
    @BindView(R.id.expire_date)
    EditText mExpireDate;

    String firstName;
    String idNumber;
    String bankAccount;
    String expireDate;

    RequestQueue mRequestQueue;
    StringRequest mStringRequest;


    public final String TAG = "AddBankAccountActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank_account);

        ButterKnife.bind(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-RobotoRegular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.add_bank));


        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);

        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.titleview, null);

        ((TextView) v.findViewById(R.id.title)).setText(getString(R.string.add_bank));

        this.getSupportActionBar().setCustomView(v);

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bankAccount = mBankAccountEditText.getText().toString();
                firstName = mFirstNameEditText.getText().toString();
                idNumber = mIdNumberEditText.getText().toString();
                expireDate = mExpireDate.getText().toString();

                if (!validateFields(bankAccount) || !validateFields(firstName) || !validateFields(idNumber) || !validateFields(expireDate)){
                    showSnackBarMessage(getString(R.string.valid_details));
                } else {
                    View currentFocus = (AddBankAccountActivity.this).getCurrentFocus();
                    if (currentFocus != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                    ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        mParentLayout.setVisibility(View.INVISIBLE);
                        mProgressBar.setVisibility(View.VISIBLE);
                        mRequestQueue.add(mStringRequest);
                    } else {
                        showSnackBarMessage(getString(R.string.no_internet));
                    }
                }
            }
        });


        mRequestQueue = Volley.newRequestQueue(this);


        mStringRequest = new StringRequest(Request.Method.POST, ContractClass.ADD_BANK_ACCOUNT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("response",response);
                //TODO : TO BE TESTED
                mParentLayout.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                if (response != null && isJSONValid(response)) {
                    if (response.contains("false")) {
                        finish();
                        showSnackBarMessage(getString(R.string.error));
                    } else {
                        showSnackBarMessage(getString(R.string.register_successful));
                        mBankAccountEditText.setText("");
                        mExpireDate.setText("");
                        mFirstNameEditText.setText("");
                        mIdNumberEditText.setText("");
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
                stringStringMap.put("bank_name",firstName);
                stringStringMap.put("bank_account",bankAccount);
                stringStringMap.put("national_id",idNumber);
                stringStringMap.put("expire_date",expireDate);
                stringStringMap.put("id",getIntent().getStringExtra("id"));
                return stringStringMap;
            }
        };


        mStringRequest.setTag(TAG);

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
