package com.hamidi.ecommerce.product;

import jakarta.validation.constraints.NotNull;

public record PurchaseRequest(
        @NotNull(message = "Product is mendatory")
        Integer productId,
        @NotNull(message = "Quantity is mendatory")
        double quantity
) {
}
