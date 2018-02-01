package com.paymentsolutions.paymentsolutions;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class StoresActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.categories_toggle)
    ToggleButton categoriesToggle;
    @BindView(R.id.all_stores_toggle)
    ToggleButton allStoresToggle;
    @BindView(R.id.categories)
    RecyclerView categories;
    @BindView(R.id.all_stores)
    RecyclerView allStores;
    @BindView(R.id.progress_indicator)
    ProgressBar mProgressIndicator;
    @BindView(R.id.parent_layout)
    View mParentLayout;

    boolean checked = true;
    ArrayList<MenuItem> menuItems;
    ArrayList<MenuItem> menuItems1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stores);
        ButterKnife.bind(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-RobotoRegular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        setSupportActionBar(toolbar);
        setTitle("Stores");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);

        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.titleview, null);

        ((TextView)v.findViewById(R.id.title)).setText(this.getTitle());

        this.getSupportActionBar().setCustomView(v);

        menuItems = new ArrayList<>();

        MenuAdapter menuAdapter = new MenuAdapter(menuItems, new MenuAdapter.OnItemClick() {
            @Override
            public void setOnItemClick(int position) {

            }
        });
        int numberOfColumns = 3;

        categories.setLayoutManager(new GridLayoutManager(StoresActivity.this, numberOfColumns));
        categories.setAdapter(menuAdapter);

        menuItems1 = new ArrayList<>();

        MenuAdapter menuAdapter1 = new MenuAdapter(menuItems1, new MenuAdapter.OnItemClick() {
            @Override
            public void setOnItemClick(int position) {

            }
        });

        allStores.setLayoutManager(new GridLayoutManager(StoresActivity.this, numberOfColumns));
        allStores.setAdapter(menuAdapter1);

        categoriesToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checked) {
                    categoriesToggle.setBackgroundResource(R.drawable.my_button);
                    allStoresToggle.setBackgroundResource(R.drawable.my_button5);
                    allStores.setVisibility(View.GONE);
                    categories.setVisibility(View.VISIBLE);
                    checked = true;
                }
            }
        });

        allStoresToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checked) {
                    allStoresToggle.setBackgroundResource(R.drawable.my_button);
                    categoriesToggle.setBackgroundResource(R.drawable.my_button5);
                    categories.setVisibility(View.GONE);
                    allStores.setVisibility(View.VISIBLE);
                    checked = false;
                }
            }
        });

        new CategoriesAsyncTask().execute(ContractClass.GET_CATEGORY_URL);
        new AllStoresAsyncTask().execute(ContractClass.GET_VENDORS_URL);

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
                    showSnackBarMessage("Error");
                    finish();
                } else {
                    mParentLayout.setVisibility(View.VISIBLE);
                    mProgressIndicator.setVisibility(View.GONE);
                    try {
                        JSONObject jsonObject = new JSONObject(jsonResponse);
                        JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                        JSONArray jsonArray = jsonObject1.getJSONArray("category");
                        for (int i = 0;i<jsonArray.length();i++){
                            JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                            String id = jsonObject2.getString("id");
                            String name = jsonObject2.getString("name");
                            menuItems.add(new MenuItem(id,name,R.drawable.ic_email_black_24dp));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                finish();
                showSnackBarMessage("Error.Please Try Again");
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

    public class AllStoresAsyncTask extends AsyncTask<String, Void, String> {

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
                Log.i("All Stores", jsonResponse);
                if (jsonResponse.contains("false")) {
                    showSnackBarMessage("Error");
                    finish();
                } else {
                    mParentLayout.setVisibility(View.VISIBLE);
                    mProgressIndicator.setVisibility(View.GONE);
                    try {
                        JSONObject jsonObject = new JSONObject(jsonResponse);
                        JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                        JSONArray jsonArray = jsonObject1.getJSONArray("user");
                        for (int i = 0;i<jsonArray.length();i++){
                            JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                            String id = jsonObject2.getString("id");
                            String name = jsonObject2.getString("name");
                            menuItems1.add(new MenuItem(id,name,R.drawable.ic_email_black_24dp));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                finish();
                showSnackBarMessage("Error.Please Try Again");
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
