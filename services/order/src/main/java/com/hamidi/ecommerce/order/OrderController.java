package com.hamidi.ecommerce.order;

import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @PostMapping
    public ResponseEntity<Integer> createOrder(
            @RequestBody @Valid OrderRequest request
    ) {
        return ResponseEntity.ok(service.createOrder(request));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{order-id}")
    public ResponseEntity<OrderResponse> findById(
            @PathVariable("order-id")  Integer customerId
    ) {
        return ResponseEntity.ok(service.findById(customerId));
    }
}
