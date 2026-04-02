package com.hamidi.ecommerce.payment;

import com.hamidi.ecommerce.customer.CustomerResponse;
import com.hamidi.ecommerce.order.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal amount,
        PaymentMethod paymentMethod,
        Integer orderId,
        String orderReference,
        CustomerResponse customer
) {
}
