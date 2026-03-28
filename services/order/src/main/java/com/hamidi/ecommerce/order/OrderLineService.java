package com.hamidi.ecommerce.order;

import com.hamidi.ecommerce.orderline.OrderLineMapper;
import com.hamidi.ecommerce.orderline.OrderLineRepository;
import com.hamidi.ecommerce.orderline.OrderLineRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderLineService {

    private final OrderLineRepository repository;
    private final OrderLineMapper mapper;

    public Integer saveOrderLine(OrderLineRequest request) {
        var order = mapper.toOrderLine(request);
        return repository.save(order).getId();
    }
}
