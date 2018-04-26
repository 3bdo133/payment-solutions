package com.paymentsolutions.paymentsolutions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

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
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.name)
    EditText mName;
    @BindView(R.id.add_photo)
    TextView mAddPhoto;
    @BindView(R.id.email)
    EditText mEmail;
    @BindView(R.id.mobile)
    EditText mMobile;
    @BindView(R.id.address)
    EditText mAddress;
    @BindView(R.id.password)
    EditText mPassword;
    @BindView(R.id.sign_up)
    Button mSignup;
    @BindView(R.id.parent_layout)
    View mParentLayout;
    @BindView(R.id.progress_indicator)
    View mProgressIndicator;
    @BindView(R.id.photo)
    ImageView mPhoto;
    @BindView(R.id.code)
    EditText mCode;
    @BindView(R.id.code_parent)
    View mCodeParent;
    @BindView(R.id.enter_code)
    Button mEnterCode;
    String email;
    String password;
    String name;
    String mobile;
    String address;
    String token;

    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;

    FirebaseAuth mAuth;

    boolean mSignInProgress = false;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-RobotoRegular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        SharedPreferences preferences1 = PreferenceManager.getDefaultSharedPreferences(this);
        token = preferences1.getString("token","error");
        Log.i("token",token);

        mAuth = FirebaseAuth.getInstance();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String lang = preferences.getString("lang", "error");

        if (lang.equals("ar")) {
            mAddPhoto.setText(Html.fromHtml("اضافة <br> صورة"));
        } else if (lang.equals("en")) {
            mAddPhoto.setText(Html.fromHtml("Add <br> Photo"));
        } else {
            if (Locale.getDefault().getLanguage().equals("ar"))
                mAddPhoto.setText(Html.fromHtml("اضافة <br> صورة"));
            else
                mAddPhoto.setText(Html.fromHtml("Add <br> Photo"));
        }

        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = mEmail.getText().toString().trim();
                password = mPassword.getText().toString().trim();
                name = mName.getText().toString().trim();
                mobile = mMobile.getText().toString().trim();
                address = mAddress.getText().toString().trim();
                if (!validateEmail(email) && !validateFields(password) && !validateFields(name) && !validateMobile(mobile) && !validateFields(address)) {
                    showSnackBarMessage(getString(R.string.valid_details));
                } else if (!validateEmail(email)) {
                    showSnackBarMessage(getString(R.string.email_valid));
                } else if (!validateFields(password)) {
                    showSnackBarMessage(getString(R.string.password_valid));
                } else if (!validateFields(name)) {
                    showSnackBarMessage(getString(R.string.name_valid));
                } else if (!validateMobile(mobile)) {
                    showSnackBarMessage(getString(R.string.mobile_valid));
                } else if (!validateFields(address)) {
                    showSnackBarMessage(getString(R.string.address_valid));
                } else {
                    // Check if no view has focus:
                    View currentFocus = (RegisterActivity.this).getCurrentFocus();
                    if (currentFocus != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        //new RegisterAsyncTask().execute(ContractClass.REGISTER_URL);
                        mAuth.useAppLanguage();
                        mProgressIndicator.setVisibility(View.VISIBLE);
                        mAddPhoto.setVisibility(View.GONE);
                        mAddress.setVisibility(View.GONE);
                        mEmail.setVisibility(View.GONE);
                        mMobile.setVisibility(View.GONE);
                        mName.setVisibility(View.GONE);
                        mPassword.setVisibility(View.GONE);
                        mPhoto.setVisibility(View.GONE);
                        mSignup.setVisibility(View.GONE);
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                "+20"+mobile,        // Phone number to verify
                                5,                 // Timeout duration
                                TimeUnit.SECONDS,   // Unit of timeout
                                RegisterActivity.this,               // Activity (for callback binding)
                                mCallbacks);        // OnVerificationStateChangedCallback

                        mSignInProgress = true;
                    } else {
                        showSnackBarMessage(getString(R.string.no_internet));
                    }
                }
            }
        });


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d("Register Activity", "onVerificationCompleted:" + phoneAuthCredential);
                showSnackBarMessage(getString(R.string.verified));
                mSignInProgress = false;
                new RegisterAsyncTask().execute(ContractClass.REGISTER_URL);


            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w("Register Activity", "onVerificationFailed", e);
                showSnackBarMessage(getString(R.string.mobile_number));
                mProgressIndicator.setVisibility(View.GONE);
                mAddPhoto.setVisibility(View.VISIBLE);
                mAddress.setVisibility(View.VISIBLE);
                mEmail.setVisibility(View.VISIBLE);
                mMobile.setVisibility(View.VISIBLE);
                mName.setVisibility(View.VISIBLE);
                mPassword.setVisibility(View.VISIBLE);
                mPhoto.setVisibility(View.VISIBLE);
                mSignup.setVisibility(View.VISIBLE);
                mSignInProgress = false;

            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("Register Activity", "onCodeSent:" + s);

                mVerificationId = s;
                mResendToken = forceResendingToken;

                Log.d("Register Activity", "");

                mProgressIndicator.setVisibility(View.GONE);
                mCodeParent.setVisibility(View.VISIBLE);

            }
        };

        mEnterCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = mCode.getText().toString();
                if (!validateFields(code)){
                    showSnackBarMessage(getString(R.string.enter_valid_code));
                } else {
                    mProgressIndicator.setVisibility(View.VISIBLE);
                    mAddPhoto.setVisibility(View.GONE);
                    mAddress.setVisibility(View.GONE);
                    mEmail.setVisibility(View.GONE);
                    mMobile.setVisibility(View.GONE);
                    mName.setVisibility(View.GONE);
                    mPassword.setVisibility(View.GONE);
                    mPhoto.setVisibility(View.GONE);
                    mSignup.setVisibility(View.GONE);
                    mCodeParent.setVisibility(View.INVISIBLE);
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Register Activity", "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            showSnackBarMessage(getString(R.string.verified));
                            new RegisterAsyncTask().execute(ContractClass.REGISTER_URL);
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            mProgressIndicator.setVisibility(View.GONE);
                            mCodeParent.setVisibility(View.VISIBLE);
                            showSnackBarMessage(getString(R.string.error_in_code));
                            Log.w("Register Activity", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    public static boolean validateFields(String name) {

        return !TextUtils.isEmpty(name);
    }

    public static boolean validateEmail(String string) {

        return !(TextUtils.isEmpty(string) || !Patterns.EMAIL_ADDRESS.matcher(string).matches());
    }

    public static boolean validateMobile(String string) {

        return !(TextUtils.isEmpty(string) || string.length() != 11);
    }

    private void showSnackBarMessage(String message) {

        if (findViewById(android.R.id.content) != null) {

            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
        }
    }

    public class RegisterAsyncTask extends AsyncTask<String, Void, String> {

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
                params.put("email", email);
                params.put("address", address);
                params.put("mobile", mobile);
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
                if (jsonResponse.contains("false")) {
                    mProgressIndicator.setVisibility(View.INVISIBLE);
                    mParentLayout.setVisibility(View.VISIBLE);
                    mProgressIndicator.setVisibility(View.GONE);
                    mAddPhoto.setVisibility(View.VISIBLE);
                    mAddress.setVisibility(View.VISIBLE);
                    mEmail.setVisibility(View.VISIBLE);
                    mMobile.setVisibility(View.VISIBLE);
                    mName.setVisibility(View.VISIBLE);
                    mPassword.setVisibility(View.VISIBLE);
                    mPhoto.setVisibility(View.VISIBLE);
                    mSignup.setVisibility(View.VISIBLE);
                    mCodeParent.setVisibility(View.GONE);
                    showSnackBarMessage(getString(R.string.email_or_mobile));
                } else {
                    Log.i("response",jsonResponse);
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("case", "done");
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }
            } else {
                mProgressIndicator.setVisibility(View.INVISIBLE);
                mParentLayout.setVisibility(View.VISIBLE);
                mProgressIndicator.setVisibility(View.GONE);
                mAddPhoto.setVisibility(View.VISIBLE);
                mAddress.setVisibility(View.VISIBLE);
                mEmail.setVisibility(View.VISIBLE);
                mMobile.setVisibility(View.VISIBLE);
                mName.setVisibility(View.VISIBLE);
                mPassword.setVisibility(View.VISIBLE);
                mPhoto.setVisibility(View.VISIBLE);
                mSignup.setVisibility(View.VISIBLE);
                mCodeParent.setVisibility(View.GONE);
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("signInProcess",mSignInProgress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mSignInProgress = savedInstanceState.getBoolean("signInProcess");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mSignInProgress){
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    mobile,        // Phone number to verify
                    5,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    RegisterActivity.this,               // Activity (for callback binding)
                    mCallbacks);        // OnVerificationStateChangedCallback
        }
    }

}
