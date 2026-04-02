package com.hamidi.ecommerce.kafka.order;

import com.hamidi.ecommerce.kafka.payment.PaymentMethod;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation(
        String orderReference,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        Customer customer,
        List<Product> products
) {
}
