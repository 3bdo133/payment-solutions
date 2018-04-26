package com.paymentsolutions.paymentsolutions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.paymentsolutions.paymentsolutions.RegisterActivity.validateEmail;
import static com.paymentsolutions.paymentsolutions.RegisterActivity.validateFields;

public class AddProductActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.add_photo_text)
    TextView getmState;
    @BindView(R.id.photo)
    ImageView mPhoto;
    @BindView(R.id.spinner1)
    Spinner spinner;
    @BindView(R.id.spinner2)
    Spinner spinner1;
    @BindView(R.id.progress_indicator)
    ProgressBar mProgressIndicator;
    @BindView(R.id.parent_layout)
    View mParentLayout;
    @BindView(R.id.reminder)
    SwitchCompat switchCompat;
    @BindView(R.id.state)
    TextView mState;
    @BindView(R.id.add_product)
    Button mAddProduct;
    @BindView(R.id.name)
    EditText mName;
    @BindView(R.id.quantity)
    EditText mQuantity;
    @BindView(R.id.price)
    EditText mPrice;
    @BindView(R.id.description)
    EditText mDescription;

    int mSwitch = 1;
    int spinnerSelected = -1;

    String name;
    String quantity;
    String price;
    String description;
    String id;
    String productId;

    ArrayList<String> categories;
    ArrayList<Integer> categoriesIds;

    ArrayList<String> currencies;

    ArrayAdapter<String> adapter;
    ArrayAdapter<String> adapter1;

    RequestQueue mRequestQueue;
    StringRequest mStringRequest;

    public final String TAG = "AddProductActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        ButterKnife.bind(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-RobotoRegular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.add_product));

        id = getIntent().getStringExtra("id");


        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);

        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.titleview, null);

        ((TextView) v.findViewById(R.id.title)).setText(getString(R.string.add_product));

        this.getSupportActionBar().setCustomView(v);

        mRequestQueue = Volley.newRequestQueue(this);

        categories = new ArrayList<>();
        categories.add(getString(R.string.select_category));
        currencies = new ArrayList<>();
        categoriesIds = new ArrayList<>();
        categoriesIds.add(-1);

        mAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                description = mDescription.getText().toString().trim();
                price = mPrice.getText().toString().trim();
                name = mName.getText().toString().trim();
                quantity = mQuantity.getText().toString().trim();
                if (!validateFields(price) && !validateFields(name) && !validateFields(description) && !validateFields(quantity)) {
                    showSnackBarMessage(getString(R.string.valid_details));
                } else if (!validateFields(price)) {
                    showSnackBarMessage(getString(R.string.price_empty));
                } else if (!validateFields(name)) {
                    showSnackBarMessage(getString(R.string.name_empty));
                } else if (!validateFields(description)) {
                    showSnackBarMessage(getString(R.string.desc_empty));
                } else if (!validateFields(quantity)) {
                    showSnackBarMessage(getString(R.string.qunatity_empty));
                } else if (spinnerSelected == -1) {
                    showSnackBarMessage(getString(R.string.select_category));
                } else {
                    // Check if no view has focus:
                    View currentFocus = (AddProductActivity.this).getCurrentFocus();
                    if (currentFocus != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected() && !mAddProduct.getText().toString().equals(getString(R.string.edit_product))) {
                        new AddProductAsyncTask().execute(ContractClass.ADD_PRODUCT_URL);
                    } else if (networkInfo != null && networkInfo.isConnected()){
                        mParentLayout.setVisibility(View.INVISIBLE);
                        mProgressIndicator.setVisibility(View.VISIBLE);
                        mRequestQueue.add(mStringRequest);
                    } else {
                        showSnackBarMessage(getString(R.string.no_internet));
                    }
                }
            }
        });

        adapter = new ArrayAdapter<>(
                this, R.layout.spinner_item, categories);
        adapter.setDropDownViewResource(R.layout.spinner_item_drop);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerSelected = categoriesIds.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                spinnerSelected = -1;
            }
        });

        adapter1 = new ArrayAdapter<>(
                this, R.layout.spinner_item, currencies);
        adapter1.setDropDownViewResource(R.layout.spinner_item_drop);

        spinner1.setAdapter(adapter1);

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mState.setText(getString(R.string.product_available));
                    mState.setTextColor(Color.GREEN);
                    mSwitch = 1;
                } else {
                    mState.setText(getString(R.string.unavailable));
                    mState.setTextColor(Color.RED);
                    mSwitch = 0;
                }
            }
        });

        new CurrencyAsyncTask().execute(ContractClass.GET_CURRENCY_URL);
        new CategoriesAsyncTask().execute(ContractClass.GET_CATEGORY_URL);

        if (getIntent().hasExtra("category_id")) {
            mName.setText(getIntent().getStringExtra("name"));
            mPrice.setText(getIntent().getStringExtra("price").replaceAll(" EGP",""));
            mQuantity.setText(getIntent().getStringExtra("quantity"));
            spinner.setSelection(categoriesIds.indexOf(Integer.parseInt(getIntent().getStringExtra("category_id"))));
            Log.i("image",getIntent().getStringExtra("image")+"   ");
            if (!TextUtils.isEmpty(getIntent().getStringExtra("image"))) {
                Picasso.with(this).load(getIntent().getStringExtra("image")).into(mPhoto);
            }
            getmState.setVisibility(View.GONE);
            mAddProduct.setText(getString(R.string.edit_product));
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(getString(R.string.edit_product));

             productId = getIntent().getStringExtra("product_id");


            this.getSupportActionBar().setDisplayShowCustomEnabled(true);
            this.getSupportActionBar().setDisplayShowTitleEnabled(false);

            LayoutInflater inflater = LayoutInflater.from(this);
            View view = inflater.inflate(R.layout.titleview, null);

            ((TextView) v.findViewById(R.id.title)).setText(getString(R.string.edit_product));

            this.getSupportActionBar().setCustomView(view);

            Log.i("title",getIntent().getStringExtra("title"));
        }


        mStringRequest = new StringRequest(Request.Method.POST, ContractClass.EDIT_PRODUCT_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mParentLayout.setVisibility(View.VISIBLE);
                mProgressIndicator.setVisibility(View.INVISIBLE);
                Log.i("response",response);
                if (response != null && isJSONValid(response)) {
                    if (response.contains("false")) {
                        finish();
                        showSnackBarMessage(getString(R.string.error));
                    } else {
                        showSnackBarMessage(getString(R.string.update_successful));
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mParentLayout.setVisibility(View.VISIBLE);
                mProgressIndicator.setVisibility(View.INVISIBLE);
                showSnackBarMessage(getString(R.string.error_try_again));
                error.printStackTrace();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> stringStringMap = new HashMap<>();
                stringStringMap.put("id",productId);
                stringStringMap.put("name",getIntent().getStringExtra("name"));
                stringStringMap.put("category_id",getIntent().getStringExtra("category_id"));
                stringStringMap.put("category_name",getIntent().getStringExtra("category_name"));
                stringStringMap.put("user_id",id);
                stringStringMap.put("user_name",getIntent().getStringExtra("title"));
                stringStringMap.put("price",getIntent().getStringExtra("price"));
                stringStringMap.put("quantity",getIntent().getStringExtra("quantity"));
                return stringStringMap;
            }
        };

        mStringRequest.setTag(TAG);

    }

    public class CategoriesAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mParentLayout.setVisibility(View.GONE);
            mProgressIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            String stringUrl = strings[0];
            URL url;
            HttpsURLConnection conn = null;
            InputStream in = null;
            try {
                url = new URL(stringUrl);
                conn = (HttpsURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.connect();
                in = conn.getInputStream();

                Scanner scanner = new Scanner(in);
                scanner.useDelimiter("\\A");

                boolean hasInput = scanner.hasNext();
                if (hasInput) {
                    return scanner.next();
                } else {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }


        @Override
        protected void onPostExecute(String jsonResponse) {
            super.onPostExecute(jsonResponse);
            if (jsonResponse != null && isJSONValid(jsonResponse)) {
                Log.i("Categories", jsonResponse);
                if (jsonResponse.contains("false")) {
                    showSnackBarMessage(getString(R.string.error));
                    finish();
                } else {
                    mParentLayout.setVisibility(View.VISIBLE);
                    mProgressIndicator.setVisibility(View.GONE);
                    try {
                        JSONObject jsonObject = new JSONObject(jsonResponse);
                        JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                        JSONArray jsonArray = jsonObject1.getJSONArray("category");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                            String id = jsonObject2.getString("id");
                            String name = jsonObject2.getString("name");
                            categories.add(name);
                            categoriesIds.add(Integer.valueOf(id));
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (getIntent().hasExtra("category_id")) {
                        spinner.setSelection(categoriesIds.indexOf(Integer.parseInt(getIntent().getStringExtra("category_id"))));
                    }
                }
            } else {
                finish();
                showSnackBarMessage(getString(R.string.error_try_again));
            }
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
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public class CurrencyAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mParentLayout.setVisibility(View.GONE);
            mProgressIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            String stringUrl = strings[0];
            URL url;
            HttpsURLConnection conn = null;
            InputStream in = null;
            try {
                url = new URL(stringUrl);
                conn = (HttpsURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.connect();
                in = conn.getInputStream();

                Scanner scanner = new Scanner(in);
                scanner.useDelimiter("\\A");

                boolean hasInput = scanner.hasNext();
                if (hasInput) {
                    return scanner.next();
                } else {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }


        @Override
        protected void onPostExecute(String jsonResponse) {
            super.onPostExecute(jsonResponse);
            if (jsonResponse != null && isJSONValid(jsonResponse)) {
                Log.i("Categories", jsonResponse);
                if (jsonResponse.contains("false")) {
                    showSnackBarMessage(getString(R.string.error));
                    finish();
                } else {
                    mParentLayout.setVisibility(View.VISIBLE);
                    mProgressIndicator.setVisibility(View.GONE);
                    try {
                        JSONObject jsonObject = new JSONObject(jsonResponse);
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                            String name = jsonObject2.getString("L.E");
                            currencies.add(name);
                        }
                        adapter1.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                finish();
                showSnackBarMessage(getString(R.string.error_try_again));
            }
        }
    }

    public class AddProductAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mParentLayout.setVisibility(View.GONE);
            mProgressIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            String stringUrl = strings[0];
            URL url;
            HttpsURLConnection conn = null;
            InputStream in = null;
            try {
                url = new URL(stringUrl);
                conn = (HttpsURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                HashMap<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("user_id", id);
                params.put("price", price);
                params.put("quantity", quantity);
                params.put("description", description);
                params.put("status", String.valueOf(mSwitch));
                params.put("category_id", String.valueOf(spinnerSelected));
                params.put("quantity",quantity);


                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(params));
                writer.flush();
                writer.close();
                os.close();

                conn.connect();
                in = conn.getInputStream();

                Scanner scanner = new Scanner(in);
                scanner.useDelimiter("\\A");

                boolean hasInput = scanner.hasNext();
                if (hasInput) {
                    return scanner.next();
                } else {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }


        @Override
        protected void onPostExecute(String jsonResponse) {
            super.onPostExecute(jsonResponse);
            if (jsonResponse != null && isJSONValid(jsonResponse)) {
                Log.i("Add", jsonResponse);
                if (jsonResponse.contains("false")) {
                    mProgressIndicator.setVisibility(View.INVISIBLE);
                    mParentLayout.setVisibility(View.VISIBLE);
                    showSnackBarMessage(getString(R.string.error));
                } else {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("case", "done");
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }
            } else {
                mProgressIndicator.setVisibility(View.INVISIBLE);
                mParentLayout.setVisibility(View.VISIBLE);
                showSnackBarMessage(getString(R.string.error_try_again));
            }
        }


        private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }

            return result.toString();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (getIntent().hasExtra("category_id"))
                    finish();
                else
                    super.onBackPressed();
                break;
        }
        return true;
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mRequestQueue != null){
            mRequestQueue.cancelAll(TAG);
        }
    }
}
