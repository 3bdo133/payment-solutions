package com.paymentsolutions.paymentsolutions;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity {

    public final static int REQUEST_CODE = 1;

    @BindView(R.id.sign_up)
    TextView mSignUp;
    @BindView(R.id.email_login)
    Button mSignIn;
    @BindView(R.id.logo)
    ImageView mLogo;
    @BindView(R.id.email)
    EditText mEmail;
    @BindView(R.id.password)
    EditText mPassword;
    @BindView(R.id.parent_layout)
    View mParentLayout;
    @BindView(R.id.progress_indicator)
    ProgressBar mProgressIndicator;
    @BindView(R.id.facebook_login)
    Button mLoginButton;
    CallbackManager callbackManager;
    private String email;
    private String password;
    String token;
    private static final String EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        callbackManager = CallbackManager.Factory.create();

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "email", "user_birthday", "user_friends"));
            }
        });

        SharedPreferences preferences1 = PreferenceManager.getDefaultSharedPreferences(this);
        token = preferences1.getString("token","error");
        Log.i("token",token);


        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());

                                // Application code
                                try {
                                    String email = object.getString("email");
                                    Log.v("LoginActivity", email);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender,birthday,friends");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });


        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-RobotoRegular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                mEmail.setText("");
                mPassword.setText("");
            }
        });

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String lang = preferences.getString("lang", "error");

        if (lang.equals("en")) {
            mLogo.setImageResource(R.drawable.logo_english);
            setLocale("en");
        } else if (lang.equals("ar")) {
            mLogo.setImageResource(R.drawable.arabic);
            setLocale("ar");
        } else {
            if (Locale.getDefault().getLanguage().equals("ar")) {
                mLogo.setImageResource(R.drawable.arabic);
                setLocale("ar");
            } else {
                mLogo.setImageResource(R.drawable.logo_english);
                setLocale("en");
            }
        }

        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = mEmail.getText().toString().trim();
                password = mPassword.getText().toString().trim();
                if (!validateEmail(email) && !validateFields(password)) {
                    showSnackBarMessage(getString(R.string.valid_details));
                } else if (!validateEmail(email)) {
                    showSnackBarMessage(getString(R.string.email_valid));
                } else if (!validateFields(password)) {
                    showSnackBarMessage(getString(R.string.password_valid));
                } else {
                    // Check if no view has focus:
                    View currentFocus = (LoginActivity.this).getCurrentFocus();
                    if (currentFocus != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        new LoginAsyncTask().execute(ContractClass.LOGIN_URL);
                    } else {
                        showSnackBarMessage(getString(R.string.no_internet));
                    }
                }
            }
        });


        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.paymentsolutions.paymentsolutions",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

    }

    public static boolean validateFields(String name) {

        return !TextUtils.isEmpty(name);
    }

    public static boolean validateEmail(String string) {

        return !(TextUtils.isEmpty(string) || !Patterns.EMAIL_ADDRESS.matcher(string).matches());
    }

    private void showSnackBarMessage(String message) {

        if (findViewById(android.R.id.content) != null) {

            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
        }
    }

    public class LoginAsyncTask extends AsyncTask<String, Void, String> {

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
                params.put("username", email);
                params.put("password", password);
                params.put("token",token);

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
                Log.i("Login", jsonResponse);
                if (jsonResponse.contains("false")) {
                    mProgressIndicator.setVisibility(View.INVISIBLE);
                    mParentLayout.setVisibility(View.VISIBLE);
                    showSnackBarMessage(getString(R.string.email_or_password));
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(jsonResponse);
                        JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                        JSONObject jsonObject2 = jsonObject1.getJSONObject("user");
                        String role = jsonObject2.getString("role");
                        String id = jsonObject2.getString("id");
                        String name = jsonObject2.getString("name");
                        String address = jsonObject2.getString("address");
                        String amount = jsonObject2.getString("total_amount");
                        String email = jsonObject2.getString("username");
                        String mobile = jsonObject2.getString("mobile");
                        String cityId = jsonObject2.getString("city_id");
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.putExtra("role", role);
                        intent.putExtra("id", id);
                        intent.putExtra("amount", amount);
                        intent.putExtra("name", name);
                        intent.putExtra("address", address);
                        intent.putExtra("email", email);
                        intent.putExtra("mobile", mobile);
                        intent.putExtra("city_id", cityId);
                        startActivity(intent);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE:
                if (data != null) {
                    if (data.getStringExtra("case").equals("done")) {
                        showSnackBarMessage(getString(R.string.register_successful));
                    }
                }
                break;
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
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String lang = preferences.getString("lang", "error");
        Log.i("lang", lang);
        if (lang.equals("en")) {
            setLocale("en");
        } else if (lang.equals("ar")) {
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
