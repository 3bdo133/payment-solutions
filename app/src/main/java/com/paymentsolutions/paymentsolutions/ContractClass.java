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

    static final String LOGIN_URL = BASE_URL + "/" + FAWATERK_PATH + "/" + API + "/" + LOGIN;

    static final String REGISTER_URL = BASE_URL + "/" + FAWATERK_PATH + "/" + API + "/" + REGISTER;

    static final String CHECK_USER_URL = BASE_URL + "/" + FAWATERK_PATH + "/" + API + "/" + CHECK_USER;

    static final String SEND_MONEY_URL = BASE_URL + "/" + FAWATERK_PATH + "/" + API + "/" + SEND_MONEY;

    static final String GET_CATEGORY_URL = BASE_URL + "/" + FAWATERK_PATH + "/" + API + "/" + GET_CATEGORY;

    static final String SEND_COMPLAIN_URL = BASE_URL + "/" + FAWATERK_PATH + "/" + API + "/" + SEND_COMPLAIN;

    static final String GET_VENDORS_URL = BASE_URL + "/" + FAWATERK_PATH + "/" + API + "/" + GET_VENDORS;

    static final String GET_CURRENCY_URL = BASE_URL + "/" + FAWATERK_PATH + "/" + API + "/" + CURRENCY;

    static final String  ADD_PRODUCT_URL = BASE_URL + "/" + FAWATERK_PATH + "/" + API + "/" + ADD_PRODUCT;

}
