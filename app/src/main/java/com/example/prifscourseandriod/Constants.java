package com.example.prifscourseandriod;

public class Constants {
    public static final String HOME_URL = "http://10.173.250.98:8080/";
    public static final String VALIDATE_USER_URL = HOME_URL + "validateUser";
    public static final String GET_ALL_RESTAURANTS_URL = HOME_URL + "allRestaurants";
    public static final String GET_ALL_DRIVERS_URL = HOME_URL + "allDrivers";
public static final String INSERT_NEW_DRIVER_URL = HOME_URL + "insertDriver";
public static final String INSERT_NEW_USER_URL = HOME_URL + "insertBasic";
    public static final String GET_CUISINE_BY_ID_URL = HOME_URL + "restaurant/%d/cuisines";

public static final String  GET_ALL_CUSTOMERS_URL = HOME_URL + "allCustomers";
public static final String GET_ALL_REVIEWS_URL = HOME_URL + "allReviews";
public static final String INSERT_NEW_REVIEW_URL = HOME_URL + "insertReview";
public static final String GET_ALL_CHATS_URL = HOME_URL + "allChats";
public static final String INSERT_CHAT_BY_ID = HOME_URL + "insertChat";
public static final String GET_ALL_FOODORDERS_URL = HOME_URL + "allOrders";
public static final String INSERT_NEW_FOODORDER_URL = HOME_URL + "insertOrder";
public static final String GET_ORDERS_BY_BUYER_ID = HOME_URL + "buyer/%d";
public static final String GET_CUISINE_BY_ORDER_ID = HOME_URL + "order/%d/cuisines";
}
