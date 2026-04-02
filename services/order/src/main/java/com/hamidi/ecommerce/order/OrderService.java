package com.hamidi.ecommerce.order;

import com.hamidi.ecommerce.customer.CustomerClient;
import com.hamidi.ecommerce.exception.BusinessException;
import com.hamidi.ecommerce.kafka.OrderConfirmation;
import com.hamidi.ecommerce.kafka.OrderProducer;
import com.hamidi.ecommerce.orderline.OrderLineRequest;
import com.hamidi.ecommerce.orderline.OrderLineService;
import com.hamidi.ecommerce.payment.PaymentClient;
import com.hamidi.ecommerce.payment.PaymentRequest;
import com.hamidi.ecommerce.product.ProductClient;
import com.hamidi.ecommerce.product.PurchaseRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;
    private final PaymentClient paymentClient;

    public Integer createOrder(OrderRequest request) {
        // 1. check the customer => OpenFeign
        var customer = this.customerClient.findCustomerById(request.customerId())
                .orElseThrow(() -> new BusinessException(
                        "Cannot create order:: No customer exists with id " +
                                request.customerId()));
        // 2. purchase the product/s => product-ms
        var purchasedProducts = productClient.purchaseProducts(request.products());
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

        /* 5. start payment process */
        var paymentRequest = new PaymentRequest(
                request.amount(),
                request.paymentMethod(),
                order.getId(),
                order.getReference(),
                customer
        );
        paymentClient.requestOrderPayment(paymentRequest);

        // 6. send the order confirmation => notification-ms (kafka)
        orderProducer.sendOrderConfirmation(
                new OrderConfirmation(
                        request.reference(),
                        request.amount(),
                        request.paymentMethod(),
                        customer,
                        purchasedProducts
                )
        );

        return order.getId();
    }

    public List<OrderResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::fromOrder)
                .toList();
    }

    public OrderResponse findById(Integer customerId) {
        return repository.findById(customerId)
                .map(mapper::fromOrder)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Order with id %d not found", customerId)));
    }
}
