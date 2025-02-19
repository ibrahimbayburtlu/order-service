package com.getirCase.order_service.constants;

public final class ApiEndpoints {
    private ApiEndpoints() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final String ORDER_BASE = "/api/orders";
    public static final String GET_ORDER = "/{id}";
    public static final String CREATE_ORDER = "";
    public static final String DELETE_ORDER = "/{id}";
    public static final String UPDATE_ORDER = "/{id}";
    public static final String CANCEL_ORDER = "/{id}/cancel";
    public static final String CUSTOMER_BASE = "/api/customers";
    public static final String CUSTOMER_PORT = "http://localhost:8080";
    public static final String CUSTOMER_URL =CUSTOMER_PORT+CUSTOMER_BASE;

    public static final String GET_CUSTOMER = "/{id}";
    public static final String UPDATE_TIER = "/{id}/tier";
    public static final String UPDATE_ORDER_COUNT = "/{id}/count";

    public static final String DISCOUNT_PORT = "http://localhost:8083";
    public static final String DISCOUNT_BASE =  "/discounts";
    public static final String GET_DISCOUNT = "/{customerTier}";
    public static final String DISCOUNT_URL = DISCOUNT_PORT+DISCOUNT_BASE;

}