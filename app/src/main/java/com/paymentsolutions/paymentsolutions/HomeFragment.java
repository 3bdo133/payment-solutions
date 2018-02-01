package com.paymentsolutions.paymentsolutions;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    @BindView(R.id.banner_slider1)
    BannerSlider bannerSlider;
    @BindView(R.id.menu_items)
    RecyclerView recyclerView;

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

        String role = getActivity().getIntent().getStringExtra("role");
        final String id = getActivity().getIntent().getStringExtra("id");
        String amount = getActivity().getIntent().getStringExtra("amount");

        List<Banner> banners = new ArrayList<>();
        bannerSlider.setDefaultIndicator(IndicatorShape.CIRCLE);
        banners.add(new DrawableBanner(R.drawable.circle));
        banners.add(new DrawableBanner(R.drawable.ic_launcher_foreground));
        banners.add(new DrawableBanner(R.drawable.ic_launcher_background));
        bannerSlider.setBanners(banners);


        final ArrayList<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem("Scan QR", R.drawable.ic_scanner_black_24dp));
        menuItems.add(new MenuItem("Send Money", R.drawable.ic_send_black_24dp));
        menuItems.add(new MenuItem("Invoices", R.drawable.ic_content_paste_black_24dp));
        if (role.equals(CUSTOMER_ROLE)) {
            menuItems.add(new MenuItem("Stores", R.drawable.ic_store_black_24dp));
        } else {
            menuItems.add(new MenuItem("Add Product", R.drawable.ic_store_black_24dp));
        }
        menuItems.add(new MenuItem("Complaint", R.drawable.ic_chat_black_24dp));
        menuItems.add(new MenuItem("Order Tracking", R.drawable.ic_local_shipping_black_24dp));
        menuItems.add(new MenuItem("Support", R.drawable.ic_help_black_24dp));
        menuItems.add(new MenuItem("Tell a Friend", R.drawable.ic_email_black_24dp));
        MenuAdapter menuAdapter = new MenuAdapter(menuItems, new MenuAdapter.OnItemClick() {
            @Override
            public void setOnItemClick(int position) {
                switch (position) {
                    case 1:
                        Intent intent = new Intent(getActivity(), SendingMoneyActivity.class);
                        intent.putExtra("id", id);
                        startActivityForResult(intent, REQUEST_CODE);
                        break;

                    case 3:
                        Intent intent1;
                        if (menuItems.get(position).getTitle().equals("Stores")) {
                            intent1 = new Intent(getActivity(), StoresActivity.class);
                            startActivity(intent1);
                        } else {
                            intent1 = new Intent(getActivity(), AddProductActivity.class);
                            intent1.putExtra("id",id);
                            startActivityForResult(intent1,REQUEST_CODE_3);
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
                }
            }
        });
        int numberOfColumns = 4;
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), numberOfColumns));
        recyclerView.setAdapter(menuAdapter);


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (data != null) {
                    if (data.getStringExtra("case").equals("done")) {
                        showSnackBarMessage("Send Successful");
                    }
                }
                break;

            case REQUEST_CODE_2:
                if (data != null) {
                    if (data.getStringExtra("case").equals("done")) {
                        showSnackBarMessage("Send Complaint Successful");
                    }
                }
                break;
            case REQUEST_CODE_3:
                if (data != null) {
                    if (data.getStringExtra("case").equals("done")) {
                        showSnackBarMessage("Product Added Successful");
                    }
                }
                break;
        }

    }

    private void showSnackBarMessage(String message) {

        if (getActivity().findViewById(android.R.id.content) != null) {

            Snackbar.make(getActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
        }
    }



}
