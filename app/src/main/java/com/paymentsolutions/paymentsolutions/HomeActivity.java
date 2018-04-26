package com.paymentsolutions.paymentsolutions;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.login.Login;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan;
import uk.co.chrisjenx.calligraphy.CalligraphyUtils;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.balance)
    TextView mBalance;
    ActionBarDrawerToggle toggle;
    View navHeaderView;
    String amount;
    String name;
    String address;
    String email;
    String mobile;
    String idGlobal;
    String cityId;
    String role;
    String balanceUpdated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");


        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setCheckedItem(R.id.nav_home);
        navigationView.setNavigationItemSelectedListener(this);
        toggle.setDrawerSlideAnimationEnabled(false);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new HomeFragment()).commit();

        navHeaderView = navigationView.getHeaderView(0);

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                overrideMenuFontsFonts(navigationView);
            }
        });


        amount = getIntent().getStringExtra("amount");
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        mobile = getIntent().getStringExtra("mobile");
        address = getIntent().getStringExtra("address");
        idGlobal = getIntent().getStringExtra("id");
        cityId = getIntent().getStringExtra("city_id");
        role = getIntent().getStringExtra("role");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String lang = preferences.getString("lang", "error");

        if (lang.equals("en")) {
            mBalance.setText(Html.fromHtml("Your Balance<br><b>" + amount + "</b> EGP"));
        } else if (lang.equals("ar")) {
            mBalance.setText(Html.fromHtml("رصيدك<br><b>" + amount + "</b> جنيه مصري"));
        } else {
            if (Locale.getDefault().getLanguage().equals("ar"))
                mBalance.setText(Html.fromHtml("رصيدك<br><b>" + amount + "</b> جنيه مصري"));
            else
                mBalance.setText(Html.fromHtml("Your Balance<br><b>" + amount + "</b> EGP"));
        }
        ((TextView) navHeaderView.findViewById(R.id.name)).setText(name);
        ((TextView) navHeaderView.findViewById(R.id.textView)).setText(address);


        // get menu from navigationView
        Menu menu = navigationView.getMenu();

        if (Integer.parseInt(role) == 3){
            menu.findItem(R.id.nav_product_sales).setVisible(false);
            menu.findItem(R.id.nav_my_customers).setVisible(false);
        }

        // find MenuItem you want to change
        MenuItem navLanguage = menu.findItem(R.id.nav_language);

        if (lang.equals("en")) {
            navLanguage.setTitle("Arabic");
        } else if (lang.equals("ar")) {
            navLanguage.setTitle("اللغة الانجليزيه");
        } else {
            if (Locale.getDefault().getLanguage().equals("ar"))
                navLanguage.setTitle("اللغة الانجليزيه");
            else
                navLanguage.setTitle("Arabic");
        }
    }

    public void overrideMenuFontsFonts(View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    overrideMenuFontsFonts(vg.getChildAt(i));
                }
            } else if (v instanceof TextView) {
                CalligraphyUtils.applyFontToTextView(this, (TextView) v, "fonts/OpenSans-Regular.ttf");
            }
        } catch (Exception e) {
            //Log it, but ins't supposed to be here.
        }
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new HomeFragment()).commit();
        } else if (id == R.id.nav_payments_history) {


        } else if (id == R.id.nav_cards_list) {

            startActivity(new Intent(HomeActivity.this, CardActivity.class).putExtra("id", idGlobal));

        } else if (id == R.id.nav_favorites) {

        } else if (id == R.id.nav_setting) {

        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(HomeActivity.this, MyProfileActivity.class);
            intent.putExtra("id", idGlobal);
            intent.putExtra("amount", amount);
            intent.putExtra("name", name);
            intent.putExtra("address", address);
            intent.putExtra("email", email);
            intent.putExtra("mobile", mobile);
            intent.putExtra("city_id", cityId);
            intent.putExtra("role", role);
            startActivityForResult(intent, 2);

        } else if (id == R.id.nav_language) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            String lang = preferences.getString("lang", "error");
            if (lang.equals("en")) {
                setLocale("ar");
                restartActivity();
            } else if (lang.equals("ar")) {
                setLocale("en");
                restartActivity();
            }
        } else if (id == R.id.nav_add_bank_account){
            startActivity(new Intent(this,AddBankAccountActivity.class).putExtra("id",idGlobal));
        } else if (id == R.id.nav_charge_my_account){
            startActivity(new Intent(this,ChargeYourAccountActivity.class).putExtra("id",idGlobal));
        } else if (id == R.id.nav_charges_list){
            startActivity(new Intent(this,ChargesListActivity.class).putExtra("id",idGlobal));
        } else if (id == R.id.nav_logout){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else if (id == R.id.nav_my_customers){
            startActivity(new Intent(this,MyCustomersActivity.class).putExtra("id",idGlobal));
        } else if (id == R.id.nav_my_transfers){
            startActivity(new Intent(this,MyTransfersActivity.class).putExtra("id",idGlobal));
        } else if (id == R.id.nav_product_sales){
            startActivity(new Intent(this,ProductSalesActivity.class).putExtra("id",idGlobal));
        } else if (id == R.id.nav_withdraw){
            startActivityForResult(new Intent(this,WithdrawsHistoryActivity.class).putExtra("id",idGlobal),5);
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onStart() {
        super.onStart();
        navigationView.setCheckedItem(R.id.nav_home);
    }

    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (data != null) {
                if (data.getStringExtra("case").equals("done")) {
                    name = data.getStringExtra("name");
                    email = data.getStringExtra("email");
                    mobile = data.getStringExtra("mobile");
                    address = data.getStringExtra("address");
                    ((TextView) navHeaderView.findViewById(R.id.name)).setText(name);
                    ((TextView) navHeaderView.findViewById(R.id.textView)).setText(address);
                    showSnackBarMessage(getString(R.string.update_successful));
                }
            }
        } else if (requestCode == 5) {
            if (data != null && data.hasExtra("case")) {
                if (data.getStringExtra("case").equals("done")) {
                    balanceUpdated = data.getStringExtra("new_balance");
                    Log.i("balance", balanceUpdated);
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                    String lang = preferences.getString("lang", "error");
                    if (lang.equals("en")) {
                        mBalance.setText(Html.fromHtml("Your Balance<br><b>" + balanceUpdated + "</b> EGP"));
                    } else if (lang.equals("ar")) {
                        mBalance.setText(Html.fromHtml("رصيدك<br><b>" + balanceUpdated + "</b> جنيه مصري"));
                    } else {
                        if (Locale.getDefault().getLanguage().equals("ar"))
                            mBalance.setText(Html.fromHtml("رصيدك<br><b>" + balanceUpdated + "</b> جنيه مصري"));
                        else
                            mBalance.setText(Html.fromHtml("Your Balance<br><b>" + balanceUpdated + "</b> EGP"));
                    }
                }
            }
        }
    }

    private void showSnackBarMessage(String message) {

        if (findViewById(android.R.id.content) != null) {

            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
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


}

