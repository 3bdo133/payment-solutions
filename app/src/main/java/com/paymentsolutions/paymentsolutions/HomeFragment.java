package com.paymentsolutions.paymentsolutions;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import ss.com.bannerslider.banners.Banner;
import ss.com.bannerslider.banners.DrawableBanner;
import ss.com.bannerslider.views.BannerSlider;
import ss.com.bannerslider.views.indicators.IndicatorShape;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    public static final String ADMIN_ROLE = "0";
    public static final String VENDOR_ROLE = "2";
    public static final String CUSTOMER_ROLE = "3";

    public final static int REQUEST_CODE = 1;
    public final static int REQUEST_CODE_2 = 2;
    public final static int REQUEST_CODE_3 = 3;
    public final static int REQUEST_CODE_4 = 4;

    @BindView(R.id.banner_slider1)
    BannerSlider bannerSlider;
    @BindView(R.id.menu_items)
    RecyclerView recyclerView;
    @BindView(R.id.parent)
    NestedScrollView scrollView;

    String balanceUpdated;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-RobotoRegular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        final String role = getActivity().getIntent().getStringExtra("role");
        final String id = getActivity().getIntent().getStringExtra("id");
        String amount = getActivity().getIntent().getStringExtra("amount");
        final String name = getActivity().getIntent().getStringExtra("name");
        String address = getActivity().getIntent().getStringExtra("address");
        final String email = getActivity().getIntent().getStringExtra("email");

        List<Banner> banners = new ArrayList<>();
        banners.add(new DrawableBanner(R.drawable.banner1));
        banners.add(new DrawableBanner(R.drawable.banner2));
        banners.add(new DrawableBanner(R.drawable.banner3));
        banners.add(new DrawableBanner(R.drawable.banner4));

        for (int i = 0 ;i<banners.size();i++){
            banners.get(i).setScaleType(ImageView.ScaleType.FIT_XY);
        }

        bannerSlider.setBanners(banners);


        final ArrayList<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem(getString(R.string.scan_qr), R.drawable.ic_scanner_black_24dp));
        menuItems.add(new MenuItem(getString(R.string.send_money_home), R.drawable.ic_send_black_24dp));
        menuItems.add(new MenuItem(getString(R.string.invoices), R.drawable.ic_content_paste_black_24dp));
        if (role.equals(CUSTOMER_ROLE)) {
            menuItems.add(new MenuItem(getString(R.string.stores), R.drawable.ic_store_black_24dp));
        } else {
            menuItems.add(new MenuItem(getString(R.string.add_product), R.drawable.ic_store_black_24dp));
        }
        menuItems.add(new MenuItem(getString(R.string.complaint), R.drawable.ic_chat_black_24dp));
        menuItems.add(new MenuItem(getString(R.string.control_panel),R.drawable.ic_store_black_24dp));
        menuItems.add(new MenuItem(getString(R.string.support), R.drawable.ic_help_black_24dp));

        if (role.equals(VENDOR_ROLE)){
            menuItems.add(new MenuItem(getString(R.string.my_products),R.drawable.ic_store_black_24dp));
        }


        MenuAdapter menuAdapter = new MenuAdapter(menuItems, new MenuAdapter.OnItemClick() {
            @Override
            public void setOnItemClick(int position) {
                if (!role.equals(VENDOR_ROLE)){
                    position ++;
                }
                switch (position) {
                    case 0:
                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_GRANTED) {
                            IntentIntegrator.forSupportFragment(HomeFragment.this).setPrompt(getString(R.string.align)).initiateScan();
                            break;
                        } else {
                            requestPermissions(new String[]{Manifest.permission.CAMERA}, 3);
                            break;
                        }
                    case 1:
                        Intent intent = new Intent(getActivity(), SendingMoneyActivity.class);
                        intent.putExtra("id", id);
                        startActivityForResult(intent, REQUEST_CODE);
                        break;

                    case 2 :
                        startActivity(new Intent(getActivity(),InvoicesHistoryActivity.class).putExtra("name",name).putExtra("email",email).putExtra("id",id).putExtra("role",role));
                        break;

                    case 3:
                        Intent intent1;
                        if (menuItems.get(position).getTitle().equals(getString(R.string.stores))) {
                            intent1 = new Intent(getActivity(), StoresActivity.class);
                            startActivity(intent1);
                        } else {
                            intent1 = new Intent(getActivity(), AddProductActivity.class);
                            intent1.putExtra("id", id);
                            startActivityForResult(intent1, REQUEST_CODE_3);
                        }
                        break;

                    case 4:
                        Intent intent2 = new Intent(getActivity(), ComplaintActivity.class);
                        intent2.putExtra("id", id);
                        startActivityForResult(intent2, REQUEST_CODE_2);
                        break;

                    case 6:
                        Intent intent3 = new Intent(getActivity(), SupportActivity.class);
                        startActivity(intent3);
                        break;
                    case 7:
                        startActivity(new Intent(getActivity(),VendorProductsActivity.class).putExtra("id",id).putExtra("title",name));
                        break;
                    case 5:
                        startActivity(new Intent(getActivity(),ControlPanelActivity.class).putExtra("id",id));
                        break;
                }
            }
        });
        int numberOfColumns = 4;
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), numberOfColumns));
        recyclerView.setAdapter(menuAdapter);

        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        scrollView.fullScroll(ScrollView.FOCUS_UP);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (data != null) {
                    if (data.getStringExtra("case").equals("done")) {
                        showSnackBarMessage(getString(R.string.send_succsessful));
                    }
                }
                break;

            case REQUEST_CODE_2:
                if (data != null) {
                    if (data.getStringExtra("case").equals("done")) {
                        showSnackBarMessage(getString(R.string.send_complaint));
                    }
                }
                break;
            case REQUEST_CODE_3:
                if (data != null) {
                    if (data.getStringExtra("case").equals("done")) {
                        showSnackBarMessage(getString(R.string.product_added));
                    }
                }
                break;

            case  REQUEST_CODE_4:
                if (data != null) {
                    if (data.getStringExtra("case").equals("done")) {
                        showSnackBarMessage(getString(R.string.invoice_status));
                    }
                }
                break;
            case IntentIntegrator.REQUEST_CODE:
                IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                if (scanningResult != null) {
                    String scanContent = scanningResult.getContents();
                    String scanFormat = scanningResult.getFormatName();
                    if (scanContent == null || scanFormat == null) {
                        showSnackBarMessage(getString(R.string.nothing));
                    } else {
                        showSnackBarMessage(scanContent + " " + scanFormat);
                        Log.i("Code", scanContent + " " + scanFormat);
                        if (Patterns.WEB_URL.matcher(scanContent).matches()){
                            openWebPage(scanContent);
                        }
                    }
                    break;
                } else {
                    showSnackBarMessage(getString(R.string.nothing));
                    break;
                }
        }

    }

    private void showSnackBarMessage(String message) {

        if (getActivity().findViewById(android.R.id.content) != null) {

            Snackbar.make(getActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case 3: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    IntentIntegrator.forSupportFragment(HomeFragment.this).setPrompt(getString(R.string.align)).initiateScan();

                    break;
                } else {
                    showSnackBarMessage(getString(R.string.camera_permission));
                    break;
                }
            }
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
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
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("lang", lang).apply();
        getActivity().getBaseContext().getResources().updateConfiguration(config,
                getActivity().getBaseContext().getResources().getDisplayMetrics());
    }


    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

}




