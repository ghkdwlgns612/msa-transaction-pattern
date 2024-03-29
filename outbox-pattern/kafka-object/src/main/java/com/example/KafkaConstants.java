package com.example;

public class KafkaConstants {
    // order-service
    public static final String ORDER_PAYMENT_CONSUMER_CONTAINER_NAME = "orderPaymentContainer";
    public static final String ORDER_STOCK_CONSUMER_CONTAINER_NAME = "orderStockContainer";

    // payment-service
    public static final String PAYMENT_TOPIC_NAME = "payment";
    public static final String PAYMENT_CONSUMER_CONTAINER_NAME = "paymentContainer";
    public static final String PAYMENT_COMPENSATION_TOPIC_NAME = "payment.compensation";
    public static final String PAYMENT_CONSUMER_COMPENSATION_CONTAINER_NAME = "paymentCompensationContainer";
    public static final String PAYMENT_SUCCESS_TOPIC_NAME = "payment.success";
    public static final String PAYMENT_DLQ_TOPIC_NAME = "payment.dlt";
    public static final String PAYMENT_DLQ_TEMPLATE_NAME = "retryPaymentRequestTemplate";

    // stock-service
    public static final String STOCK_TOPIC_NAME = "stock";
    public static final String STOCK_CONSUMER_CONTAINER_NAME = "stockContainer";
    public static final String STOCK_COMPENSATION_TOPIC_NAME = "stock.compensation";
    public static final String STOCK_CONSUMER_COMPENSATION_CONTAINER_NAME = "stockCompensationContainer";
    public static final String STOCK_SUCCESS_TOPIC_NAME = "stock.success";
    public static final String STOCK_DLQ_TOPIC_NAME = "stock.dlt";
    public static final String STOCK_DLQ_TEMPLATE_NAME = "retryStockRequestTemplate";
}
