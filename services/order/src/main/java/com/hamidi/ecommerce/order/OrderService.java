package com.hamidi.ecommerce.order;

import com.hamidi.ecommerce.customer.CustomerClient;
import com.hamidi.ecommerce.exception.BusinessException;
import com.hamidi.ecommerce.orderline.OrderLineRequest;
import com.hamidi.ecommerce.product.ProductClient;
import com.hamidi.ecommerce.product.PurchaseRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderLineService orderLineService;

    public Integer createOrder(OrderRequest request) {
        // 1. check the customer => OpenFeign
        this.customerClient.findCustomerById(request.customerId())
                .orElseThrow(() -> new BusinessException("Cannot create order:: No customer exists with id " + request.customerId()));
        // 2. purchase the product/s => product-ms
        productClient.purchaseProducts(request.products());
        // 3. persist order
        var order = this.repository.save(mapper.toOrder(request));
        // 4. persist order lines
        for (PurchaseRequest purchaseRequest : request.products()) {
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            null,
                            order.getId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity()
                    )
            );
        }
        // 5. todo -- start payment process

        // 6. send the order confirmation => notification-ms (kafka)
        return order.getId();
    }
}
