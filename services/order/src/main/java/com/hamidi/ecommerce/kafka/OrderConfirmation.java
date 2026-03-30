package com.hamidi.ecommerce.kafka;

import com.hamidi.ecommerce.customer.CustomerResponse;
import com.hamidi.ecommerce.order.PaymentMethod;
import com.hamidi.ecommerce.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation (
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<PurchaseResponse> products

) {
}