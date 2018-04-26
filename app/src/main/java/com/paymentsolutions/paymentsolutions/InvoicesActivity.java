package com.paymentsolutions.paymentsolutions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class InvoicesActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.invoices)
    RecyclerView mInvoicesRecyclerView;
    @BindView(R.id.add_item)
    TextView mAddItem;
    @BindView(R.id.shop)
    TextView mShopName;
    @BindView(R.id.email_cus)
    EditText mEmail;
    @BindView(R.id.progress_indicator)
    ProgressBar progressBar;
    @BindView(R.id.parent_layout)
    View mParentLayout;
    @BindView(R.id.sub_total_num)
    TextView mSubTotal;
    @BindView(R.id.total_num)
    TextView mTotal;
    @BindView(R.id.balance_num)
    TextView mBalance;
    @BindView(R.id.phone_state)
    ImageView mPhoneState;
    @BindView(R.id.send_progress)
    ProgressBar mSendProgress;
    @BindView(R.id.save_button)
    Button mSaveButton;

    RequestQueue queue;
    StringRequest stringRequest;
    String sendToNumber;
    boolean phoneState = false;

    String email;
    String id;
    String sendToId;
    String sentToEmail;

    ArrayList<Invoice> invoices;
    InvoicesAdapter invoicesAdapter;

    public static final String TAG = "MyTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoices);

        ButterKnife.bind(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/OpenSans-Bold.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        queue = Volley.newRequestQueue(this);


        toolbar.setTitle(getString(R.string.create_invoices));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.create_invoices));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);

        LayoutInflater inflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.titleview, null);

        ((TextView) v.findViewById(R.id.title)).setText(getString(R.string.create_invoices));

        this.getSupportActionBar().setCustomView(v);

        email = getIntent().getStringExtra("email");
        String name = getIntent().getStringExtra("name");
        id = getIntent().getStringExtra("id");

        invoices = new ArrayList<>();

        invoices.add(new Invoice("", "", "", R.drawable.ic_close_black_24dp));

        invoicesAdapter = new InvoicesAdapter(this, invoices, new InvoicesAdapter.OnItemClick() {
            @Override
            public void setOnItemClick(int position) {

            }
        }, new InvoicesAdapter.OnPriceChanged() {
            @Override
            public void setOnPriceChange(String price) {
                double total = 0;
                for (int i = 0; i < invoices.size(); i++) {
                    if (!TextUtils.isEmpty(invoices.get(i).getDescription()) && !TextUtils.isEmpty(invoices.get(i).getQuantity()) && !TextUtils.isEmpty(invoices.get(i).getPrice()) && !invoices.get(i).getDescription().equals(getString(R.string.enter_description)) && !invoices.get(i).getPrice().equals(getString(R.string.enter_price)) && !invoices.get(i).getQuantity().equals(getString(R.string.enter_quantity)))
                        total += Double.parseDouble(invoices.get(i).getPrice()) * Integer.parseInt(invoices.get(i).getQuantity());
                }
                mSubTotal.setText(String.valueOf(total));
                mBalance.setText(String.valueOf(total));
                mTotal.setText(String.valueOf(total));
            }
        }, new InvoicesAdapter.OnRemoveClicked() {
            @Override
            public void setOnRemove(int position) {
                invoices.remove(position);
                invoicesAdapter.notifyItemRemoved(position);
                if (invoices.size() == 0) {
                    mSubTotal.setText("0");
                    mBalance.setText("0");
                    mTotal.setText("0");
                    mInvoicesRecyclerView.setVisibility(View.GONE);
                    invoicesAdapter.notifyDataSetChanged();
                    invoices.clear();
                } else {
                    double total = 0;
                    for (int i = 0; i < invoices.size(); i++) {
                        if (!TextUtils.isEmpty(invoices.get(i).getDescription()) && !TextUtils.isEmpty(invoices.get(i).getQuantity()) && !TextUtils.isEmpty(invoices.get(i).getPrice()) && !invoices.get(i).getDescription().equals(getString(R.string.enter_description)) && !invoices.get(i).getPrice().equals(getString(R.string.enter_price)) && !invoices.get(i).getQuantity().equals(getString(R.string.enter_quantity)))
                            total += Double.parseDouble(invoices.get(i).getPrice()) * Integer.parseInt(invoices.get(i).getQuantity());
                    }
                    mSubTotal.setText(String.valueOf(total));
                    mBalance.setText(String.valueOf(total));
                    mTotal.setText(String.valueOf(total));
                }
            }
        }, 0);

        mInvoicesRecyclerView.setAdapter(invoicesAdapter);
        mInvoicesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mInvoicesRecyclerView.setFocusable(false);

        mInvoicesRecyclerView.setNestedScrollingEnabled(false);

        mAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invoices.add(new Invoice("", "", "", R.drawable.ic_close_black_24dp));
                if (invoices.size() != 1) {
                    invoicesAdapter.notifyItemInserted(invoices.size() - 1);
                }
                for (int i = 0; i < invoices.size(); i++) {
                    Log.i("Invoice", invoices.get(i).getDescription() + " " + invoices.get(i).getPrice() + " " + invoices.get(i).getQuantity());
                }
                if (mInvoicesRecyclerView.getVisibility() == View.GONE) {
                    mInvoicesRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        });

        mEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                sendToNumber = mEmail.getText().toString().trim();
                phoneState = false;
                if (!validateMobile(sendToNumber)) {
                    showSnackBarMessage(getString(R.string.mobile_valid));
                } else {
                    ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        new SendToAsyncTask().execute(ContractClass.CHECK_USER_URL);
                    } else {
                        showSnackBarMessage(getString(R.string.no_internet));
                    }
                }
            }
        });


        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < invoices.size(); i++) {
                    if (!invoices.get(i).getDescription().equals(getString(R.string.enter_description)) && !TextUtils.isEmpty(invoices.get(i).getDescription())
                            && !invoices.get(i).getPrice().equals(getString(R.string.enter_price)) && !TextUtils.isEmpty(invoices.get(i).getPrice()) && Double.parseDouble(invoices.get(i).getPrice()) > 0
                            && !invoices.get(i).getQuantity().equals(getString(R.string.enter_quantity)) && !TextUtils.isEmpty(invoices.get(i).getQuantity()) && Integer.parseInt(invoices.get(i).getQuantity()) > 0
                            && phoneState) {
                        if (i == invoices.size() - 1) {
                            stringRequest = new StringRequest(Request.Method.POST, ContractClass.CREATE_INVOICE_URL, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    if (response != null && isJSONValid(response) && !TextUtils.isEmpty(response)) {
                                        Log.i("response", response);
                                        if (response.contains("true")) {
                                            Intent resultIntent = new Intent();
                                            resultIntent.putExtra("case", "done");
                                            setResult(Activity.RESULT_OK, resultIntent);
                                            finish();
                                        } else {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            mParentLayout.setVisibility(View.VISIBLE);
                                            showSnackBarMessage(getString(R.string.error));
                                        }
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            }) {
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> stringStringMap = new HashMap<>();
                                    for (int i = 0; i < invoices.size(); i++) {
                                        stringStringMap.put("name[" + i + "]", invoices.get(i).getDescription());
                                        stringStringMap.put("quantity[" + i + "]", invoices.get(i).getQuantity());
                                        stringStringMap.put("price[" + i + "]", invoices.get(i).getPrice());
                                    }
                                    stringStringMap.put("username", sentToEmail);
                                    stringStringMap.put("id", id);
                                    return stringStringMap;
                                }
                            };
                            stringRequest.setTag(TAG);
                            mParentLayout.setVisibility(View.INVISIBLE);
                            progressBar.setVisibility(View.VISIBLE);
                            queue.add(stringRequest);
                        }
                    } else {
                        showSnackBarMessage(getString(R.string.check_data));
                        break;
                    }
                }
            }
        });

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
        getMenuInflater().inflate(R.menu.menu_invoices, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save) {
            for (int i = 0; i < invoices.size(); i++) {
                if (!invoices.get(i).getDescription().equals(getString(R.string.enter_description)) && !TextUtils.isEmpty(invoices.get(i).getDescription())
                        && !invoices.get(i).getPrice().equals(getString(R.string.enter_price)) && !TextUtils.isEmpty(invoices.get(i).getPrice()) && Double.parseDouble(invoices.get(i).getPrice()) > 0
                        && !invoices.get(i).getQuantity().equals(getString(R.string.enter_quantity)) && !TextUtils.isEmpty(invoices.get(i).getQuantity()) && Integer.parseInt(invoices.get(i).getQuantity()) > 0
                        && phoneState) {
                    if (i == invoices.size() - 1) {
                        stringRequest = new StringRequest(Request.Method.POST, ContractClass.CREATE_INVOICE_URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response != null && isJSONValid(response) && !TextUtils.isEmpty(response)) {
                                    Log.i("response", response);
                                    if (response.contains("true")) {
                                        Intent resultIntent = new Intent();
                                        resultIntent.putExtra("case", "done");
                                        setResult(Activity.RESULT_OK, resultIntent);
                                        finish();
                                    } else {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        mParentLayout.setVisibility(View.VISIBLE);
                                        showSnackBarMessage(getString(R.string.error));
                                    }
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> stringStringMap = new HashMap<>();
                                for (int i = 0; i < invoices.size(); i++) {
                                    stringStringMap.put("name[" + i + "]", invoices.get(i).getDescription());
                                    stringStringMap.put("quantity[" + i + "]", invoices.get(i).getQuantity());
                                    stringStringMap.put("price[" + i + "]", invoices.get(i).getPrice());
                                }
                                stringStringMap.put("username", sentToEmail);
                                stringStringMap.put("id", id);
                                return stringStringMap;
                            }
                        };
                        stringRequest.setTag(TAG);
                        mParentLayout.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.VISIBLE);
                        queue.add(stringRequest);
                    }
                } else {
                    showSnackBarMessage(getString(R.string.check_data));
                    break;
                }
            }
            return true;
        }
        return false;
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

    public static boolean validateMobile(String string) {

        return !(TextUtils.isEmpty(string) || string.length() != 11);
    }


    public class SendToAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mPhoneState.setVisibility(View.INVISIBLE);
            mSendProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            String stringUrl = strings[0];
            URL url;
            HttpURLConnection conn = null;
            InputStream in = null;
            try {
                url = new URL(stringUrl);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                HashMap<String, String> params = new HashMap<>();
                params.put("mobile", sendToNumber);

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
            mSendProgress.setVisibility(View.GONE);
            mPhoneState.setVisibility(View.VISIBLE);
            if (jsonResponse != null && isJSONValid(jsonResponse)) {
                Log.i("Send Money", jsonResponse);
                if (jsonResponse.contains("false")) {
                    mPhoneState.setImageResource(R.drawable.ic_clear_black_24dp);
                    showSnackBarMessage(getString(R.string.mobile_not_exist));
                } else {
                    mPhoneState.setImageResource(R.drawable.ic_check_black_24dp);
                    phoneState = true;
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(jsonResponse);
                        JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                        sendToId = jsonObject1.getString("id");
                        sentToEmail = jsonObject1.getString("username");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                mPhoneState.setImageResource(R.drawable.ic_clear_black_24dp);
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


}
