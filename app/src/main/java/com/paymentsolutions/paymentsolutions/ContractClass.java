package com.paymentsolutions.paymentsolutions;

import android.provider.BaseColumns;

final class ContractClass {

    private static final String BASE_URL = "https://www.fawaterk.com";

    private static final String FAWATERK_PATH = "fawaterk";

    private static final String CHECK_USER = "check_user";

    private static final String API = "api";

    private static final String LOGIN = "login";

    private static final String REGISTER = "signup";

    private static final String SEND_MONEY = "send_money";

    private static final String GET_CATEGORY = "get_category";

    private static final String SEND_COMPLAIN = "send_complain";

    private static final String GET_VENDORS = "get_vendors";

    private static final String CURRENCY = "currency";

    private static final String ADD_PRODUCT = "add_product";

    private static final String EDIT = "edit";

    private static final String GET_PRODUCTS = "get_products";

    private static final String GET_PRODUCT = "get_product";

    private static final String ADD_CHECKOUT = "add_checkout";

    private static final String VIEW_CART = "view_cart";

    private static final String CREATE_INVOICE = "create_invoice";

    private static final String GET_INVOICES = "get_invoices";

    private static final String GET_INVOICE = "get_invoice";

    static final String LOGIN_URL = BASE_URL + "/" + FAWATERK_PATH + "/" + API + "/" + LOGIN;

    static final String REGISTER_URL = BASE_URL + "/" + FAWATERK_PATH + "/" + API + "/" + REGISTER;

    static final String CHECK_USER_URL = BASE_URL + "/" + FAWATERK_PATH + "/" + API + "/" + CHECK_USER;

    static final String SEND_MONEY_URL = BASE_URL + "/" + FAWATERK_PATH + "/" + API + "/" + SEND_MONEY;

    static final String GET_CATEGORY_URL = BASE_URL + "/" + FAWATERK_PATH + "/" + API + "/" + GET_CATEGORY;

    static final String SEND_COMPLAIN_URL = BASE_URL + "/" + FAWATERK_PATH + "/" + API + "/" + SEND_COMPLAIN;

    static final String GET_VENDORS_URL = BASE_URL + "/" + FAWATERK_PATH + "/" + API + "/" + GET_VENDORS;

    static final String GET_CURRENCY_URL = BASE_URL + "/" + FAWATERK_PATH + "/" + API + "/" + CURRENCY;

    static final String  ADD_PRODUCT_URL = BASE_URL + "/" + FAWATERK_PATH + "/" + API + "/" + ADD_PRODUCT;

    static final String EDIT_PROFILE = BASE_URL + "/" + FAWATERK_PATH + "/" + API + "/" + EDIT;

    static final String GET_PRODUCTS_URL = BASE_URL + "/" + FAWATERK_PATH + "/" + API + "/" + GET_PRODUCTS;

    static final String GET_PRODUCT_URL = BASE_URL + "/" + FAWATERK_PATH + "/" + API + "/" + GET_PRODUCT;

    static final String ADD_CHECKOUT_URL = BASE_URL + "/" + FAWATERK_PATH + "/" + API + "/" + ADD_CHECKOUT;

    static final String VIEW_CART_URL = BASE_URL + "/" + FAWATERK_PATH + "/" + API + "/" + VIEW_CART;

    static final String CREATE_INVOICE_URL = BASE_URL + "/" + FAWATERK_PATH + "/" + API + "/" + CREATE_INVOICE;

    static final String GET_INVOICES_URL = BASE_URL + "/" + FAWATERK_PATH + "/" + API + "/" + GET_INVOICES;

    static final String GET_INVOICE_URL = BASE_URL + "/" + FAWATERK_PATH + "/" + API + "/" + GET_INVOICE;

    static final String EDIT_PRODUCT_URL = "https://www.fawaterk.com/fawaterk/api/edit_product";

    static final String CONTROL_PANEL_URL = "https://www.fawaterk.com/fawaterk/api/user_panel";

    static final String PRODUCT_SALES_URL = "https://www.fawaterk.com/fawaterk/api/product_analytics";

    static final String MY_CUSTOMERS_URL = "https://www.fawaterk.com/fawaterk/api/my_customers";

    static final String CHARGE_MY_ACCOUNT_URL = "https://www.fawaterk.com/fawaterk/api/charge_account";

    static final String MY_TRANSFERS_URL = "https://www.fawaterk.com/fawaterk/api/my_transfer";

    static final String MY_CHARGES_LIST = "https://www.fawaterk.com/fawaterk/api/my_charge";

    static final String MY_WITHDRAWS_URL = "https://www.fawaterk.com/fawaterk/api/my_withdraw";

    static final String WITHDRAW_URL = "https://www.fawaterk.com/fawaterk/api/withdraw";

    static final String ADD_CURRENT_CUSTOMER_URL = "https://www.fawaterk.com/fawaterk/api/add_customer_email";

    static final String ADD_BANK_ACCOUNT = "https://www.fawaterk.com/fawaterk/api/bank_account";

    static final String ADD_NEW_CUSTOMER = "https://www.fawaterk.com/fawaterk/api/add_customer";
}
