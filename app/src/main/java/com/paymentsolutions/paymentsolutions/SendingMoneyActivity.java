package com.paymentsolutions.paymentsolutions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

public class SendingMoneyActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.amount)
    EditText mAmount;
    @BindView(R.id.send_to)
    EditText mSendTo;
    @BindView(R.id.send_progress)
    ProgressBar mSendProgress;
    @BindView(R.id.send_money)
    Button mSendMoney;
    @BindView(R.id.phone_state)
    ImageView mPhoneState;
    @BindView(R.id.progress_indicator)
    ProgressBar mProgressIndicator;
    @BindView(R.id.parent_layout)
    View mParentLayout;
    @BindView(R.id.button100)
    Button mButton100;
    @BindView(R.id.button150)
    Button mButton150;
    @BindView(R.id.button200)
    Button mButton200;
    @BindView(R.id.button500)
    Button mButton500;

    boolean phoneState = false;
    String sendToNumber;
    String sendToId;
    String amount;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sending_money);
        ButterKnife.bind(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/OpenSans-Bold.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        toolbar.setTitle("Sending Money");
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sending Money");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mButton100.setOnClickListener(this);
        mButton150.setOnClickListener(this);
        mButton200.setOnClickListener(this);
        mButton500.setOnClickListener(this);

        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);

        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.titleview, null);

        ((TextView)v.findViewById(R.id.title)).setText("Sending Money");

        this.getSupportActionBar().setCustomView(v);

        id = getIntent().getStringExtra("id");

        mSendTo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                sendToNumber = mSendTo.getText().toString().trim();
                phoneState = false;
                if (!b) {
                    if (!validateMobile(sendToNumber)) {
                        showSnackBarMessage("Mobile Number should not be empty and contain 11 number");
                    } else {
                        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                        if (networkInfo != null && networkInfo.isConnected()) {
                            new SendToAsyncTask().execute(ContractClass.CHECK_USER_URL);
                        } else {
                            showSnackBarMessage("No internet connection");
                        }
                    }
                }
            }
        });


        mSendMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount = mAmount.getText().toString().trim();
                if (!validateFields(amount) && !phoneState) {
                    showSnackBarMessage("Enter Valid Details");
                } else if (!validateFields(amount)) {
                    showSnackBarMessage("Enter Amount");
                } else if (!phoneState) {
                    showSnackBarMessage("Check send to number");
                } else {
                    ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        new SendMoneyAsyncTask().execute(ContractClass.SEND_MONEY_URL);
                    } else {
                        showSnackBarMessage("No internet connection");
                    }
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button100:
                if (mAmount.getText() != null && !TextUtils.isEmpty(mAmount.getText())) {
                    int i = Integer.valueOf(mAmount.getText().toString()) + 100;
                    mAmount.setText(String.valueOf(i));
                } else {
                    mAmount.setText("100");
                }
                break;
            case R.id.button150:
                if (mAmount.getText() != null && !TextUtils.isEmpty(mAmount.getText())) {
                    int i = Integer.valueOf(mAmount.getText().toString()) + 150;
                    mAmount.setText(String.valueOf(i));
                } else {
                    mAmount.setText("150");
                }
                break;
            case R.id.button200:
                if (mAmount.getText() != null && !TextUtils.isEmpty(mAmount.getText())) {
                    int i = Integer.valueOf(mAmount.getText().toString()) + 200;
                    mAmount.setText(String.valueOf(i));
                } else {
                    mAmount.setText("200");
                }
                break;
            case R.id.button500:
                if (mAmount.getText() != null && !TextUtils.isEmpty(mAmount.getText())) {
                    int i = Integer.valueOf(mAmount.getText().toString()) + 500;
                    mAmount.setText(String.valueOf(i));
                } else {
                    mAmount.setText("500");
                }
                break;
        }
    }

    public class SendToAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                    showSnackBarMessage("Mobile Number isn't exist");
                } else {
                    mPhoneState.setImageResource(R.drawable.ic_check_black_24dp);
                    phoneState = true;
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(jsonResponse);
                        JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                        sendToId = jsonObject1.getString("id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                mPhoneState.setImageResource(R.drawable.ic_clear_black_24dp);
                showSnackBarMessage("Error.Try Again");
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


    public class SendMoneyAsyncTask extends AsyncTask<String, Void, String> {

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
                params.put("to_id", sendToId);
                params.put("user_id", id);
                params.put("money", amount);

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
                Log.i("Send Money", jsonResponse);
                if (jsonResponse.contains("false")) {
                    mParentLayout.setVisibility(View.VISIBLE);
                    mProgressIndicator.setVisibility(View.INVISIBLE);
                    showSnackBarMessage("Error in Sending Money");
                } else {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("case", "done");
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }
            } else {
                mParentLayout.setVisibility(View.VISIBLE);
                mProgressIndicator.setVisibility(View.INVISIBLE);
                showSnackBarMessage("Error.Try Again");
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


    public static boolean validateMobile(String string) {

        return !(TextUtils.isEmpty(string) || string.length() != 11);
    }

    private void showSnackBarMessage(String message) {

        if (findViewById(android.R.id.content) != null) {

            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
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

    public static boolean validateFields(String name) {

        return !TextUtils.isEmpty(name);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


}
