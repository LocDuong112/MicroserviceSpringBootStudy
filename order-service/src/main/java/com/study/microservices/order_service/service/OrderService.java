package com.study.microservices.order_service.service;

import com.study.microservices.order_service.client.InventoryClient;
import com.study.microservices.order_service.dto.OrderRequest;
import com.study.microservices.order_service.model.Order;
import com.study.microservices.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;

    public void placeOrder(OrderRequest orderRequest) {

        boolean isProductInStock = inventoryClient.isInStock(orderRequest.skuCode(), orderRequest.quantity());

        if (isProductInStock) {
            // Map OrderRequest to Order object
            Order order = new Order();
            order.setOrderNumber(UUID.randomUUID().toString());
            order.setPrice(orderRequest.price());
            order.setQuantity(orderRequest.quantity());
            order.setSkuCode(orderRequest.skuCode());

            // Save Order to OrderRepository
            orderRepository.save(order);
        } else {
            throw new RuntimeException("Product skuCode: " +  orderRequest.skuCode() + " is having quantity: " + orderRequest.quantity() + " not enough!");
        }

    }
}
