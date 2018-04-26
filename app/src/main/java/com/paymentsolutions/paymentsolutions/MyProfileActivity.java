package com.paymentsolutions.paymentsolutions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URL;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.paymentsolutions.paymentsolutions.RegisterActivity.validateEmail;
import static com.paymentsolutions.paymentsolutions.RegisterActivity.validateFields;
import static com.paymentsolutions.paymentsolutions.RegisterActivity.validateMobile;

public class MyProfileActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.f_name)
    EditText mName;
    @BindView(R.id.vendor_name)
    EditText mVendorName;
    @BindView(R.id.mobile)
    EditText mMobile;
    @BindView(R.id.email)
    EditText mEmail;
    @BindView(R.id.country)
    EditText mCountry;
    @BindView(R.id.submit)
    Button mSubmitButton;
    @BindView(R.id.vendor_name_text)
    TextView mVendorText;
    @BindView(R.id.name_text)
    TextView mNameText;
    @BindView(R.id.vendor_parent)
    View mVendorParent;
    @BindView(R.id.name_parent)
    View mNameParent;
    @BindView(R.id.parent)
    View parent;
    @BindView(R.id.progress_indicator)
    ProgressBar progressBar;
    RequestQueue queue;
    StringRequest stringRequest;
    String email;
    String name;
    String mobile;
    String address;
    String id;
    String cityId;
    String role;
    public static final String TAG = "MyTag";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        ButterKnife.bind(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/OpenSans-Bold.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        mName.setText(getIntent().getStringExtra("name"));
        mVendorName.setText(getIntent().getStringExtra("name"));
        mMobile.setText(getIntent().getStringExtra("mobile"));
        mEmail.setText(getIntent().getStringExtra("email"));
        mCountry.setText(getIntent().getStringExtra("address"));
        id = getIntent().getStringExtra("id");
        cityId = getIntent().getStringExtra("city_id");
        role = getIntent().getStringExtra("role");
        email = getIntent().getStringExtra("email");
        name = getIntent().getStringExtra("name");
        address = getIntent().getStringExtra("address");
        mobile = getIntent().getStringExtra("mobile");

        if (role.equals(HomeFragment.CUSTOMER_ROLE)) {
            mVendorParent.setVisibility(View.GONE);
            mNameParent.setVisibility(View.VISIBLE);
        } else {
            mVendorParent.setVisibility(View.VISIBLE);
            mNameParent.setVisibility(View.GONE);
        }


        toolbar.setTitle(getString(R.string.profile));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.profile));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);

        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.titleview, null);

        ((TextView) v.findViewById(R.id.title)).setText(getString(R.string.profile));

        this.getSupportActionBar().setCustomView(v);


        queue = Volley.newRequestQueue(this);

        String url = Uri.parse(ContractClass.EDIT_PROFILE).buildUpon()
                .appendQueryParameter("id", id)
                .appendQueryParameter("username", email)
                .appendQueryParameter("name", name)
                .appendQueryParameter("mobile", mobile)
                .appendQueryParameter("address", address)
                .appendQueryParameter("city_id", cityId).build().toString();
        Log.i("url",url);
        stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Result", response);
                        if (response != null && isJSONValid(response) && !TextUtils.isEmpty(response)) {
                            if (response.contains("false")) {
                                parent.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.INVISIBLE);
                                showSnackBarMessage(getString(R.string.error_update_profile));
                            } else {
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("case", "done");
                                resultIntent.putExtra("name",name);
                                resultIntent.putExtra("email",email);
                                resultIntent.putExtra("mobile",mobile);
                                resultIntent.putExtra("address",address);
                                setResult(Activity.RESULT_OK, resultIntent);
                                finish();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showSnackBarMessage(getString(R.string.error_try_again));
                finish();
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = mEmail.getText().toString().trim();
                name = mName.getText().toString().trim();
                mobile = mMobile.getText().toString().trim();
                address = mCountry.getText().toString().trim();
                if (!validateEmail(email) && !validateFields(name) && !validateMobile(mobile) && !validateFields(address)) {
                    showSnackBarMessage(getString(R.string.valid_details));
                } else if (!validateEmail(email)) {
                    showSnackBarMessage(getString(R.string.email_valid));
                } else if (!validateFields(name)) {
                    showSnackBarMessage(getString(R.string.name_valid));
                } else if (!validateMobile(mobile)) {
                    showSnackBarMessage(getString(R.string.mobile_valid));
                } else if (!validateFields(address)) {
                    showSnackBarMessage(getString(R.string.address_valid));
                } else {
                    // Check if no view has focus:
                    View currentFocus = (MyProfileActivity.this).getCurrentFocus();
                    if (currentFocus != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                    ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        parent.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.VISIBLE);
                        queue.add(stringRequest);
                    } else {
                        showSnackBarMessage(getString(R.string.no_internet));
                    }
                }
            }
        });


        stringRequest.setTag(TAG);


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
