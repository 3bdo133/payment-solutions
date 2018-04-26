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
import static com.paymentsolutions.paymentsolutions.RegisterActivity.validateMobile;

public class AddNewCustomerActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.parent_layout)
    View mParentLayout;
    @BindView(R.id.progress_indicator)
    ProgressBar mProgressBar;
    @BindView(R.id.submit)
    Button mSubmit;
    @BindView(R.id.name)
    EditText mName;
    @BindView(R.id.email)
    EditText mEmail;
    @BindView(R.id.id_number)
    EditText mIdNumber;
    @BindView(R.id.password)
    EditText mPassword;
    @BindView(R.id.mobile)
    EditText mMobile;
    @BindView(R.id.bank_name)
    EditText mBankName;
    @BindView(R.id.bank_account)
    EditText mBankAccount;


    String name;
    String email;
    String password;
    String mobile;
    String idNumber;
    String bankAccount;
    String bankName;

    RequestQueue mRequestQueue;
    StringRequest mStringRequest;


    public final String TAG = "AddNewCustomerActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_customer);

        ButterKnife.bind(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-RobotoRegular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.add_cus));


        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);

        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.titleview, null);

        ((TextView) v.findViewById(R.id.title)).setText(getString(R.string.add_cus));

        this.getSupportActionBar().setCustomView(v);

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bankAccount = mBankAccount.getText().toString();
                password = mPassword.getText().toString();
                idNumber = mIdNumber.getText().toString();
                email = mEmail.getText().toString();
                name = mName.getText().toString();
                bankName = mBankName.getText().toString();
                password = mPassword.getText().toString();

                if (!validateFields(bankAccount) || !validateFields(password) || !validateFields(idNumber) || !validateEmail(email) || !validateMobile(mobile) || !validateFields(bankName) || !validateFields(name)){
                    showSnackBarMessage(getString(R.string.valid_details));
                } else {
                    View currentFocus = (AddNewCustomerActivity.this).getCurrentFocus();
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


        mStringRequest = new StringRequest(Request.Method.POST, ContractClass.ADD_NEW_CUSTOMER, new Response.Listener<String>() {
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
                        mEmail.setText("");
                        mBankAccount.setText("");
                        mBankName.setText("");
                        mIdNumber.setText("");
                        mMobile.setText("");
                        mPassword.setText("");
                        mName.setText("");
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
                Map<String,String> stringHashMap = new HashMap<>();
                stringHashMap.put("id",getIntent().getStringExtra("id"));
                stringHashMap.put("name",name);
                stringHashMap.put("email",email);
                stringHashMap.put("mobile",mobile);
                stringHashMap.put("password",password);
                stringHashMap.put("bank_name",bankName);
                stringHashMap.put("bank_account",bankAccount);
                return stringHashMap;
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
