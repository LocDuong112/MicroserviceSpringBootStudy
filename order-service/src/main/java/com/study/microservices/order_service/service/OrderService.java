package com.study.microservices.order_service.service;

import com.study.microservices.order_service.client.InventoryClient;
import com.study.microservices.order_service.dto.OrderRequest;
import com.study.microservices.order_service.event.OrderPlacedEvent;
import com.study.microservices.order_service.model.Order;
import com.study.microservices.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

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

            // Send message to Kafka topic
            OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent(order.getOrderNumber(), orderRequest.userDetails().email());
            log.info("Start Order Placed Event {}", orderPlacedEvent);
            kafkaTemplate.send("order_placed", orderPlacedEvent);
            log.info("End  Order Placed Event {}", orderPlacedEvent);
        } else {
            throw new RuntimeException("Product skuCode: " +  orderRequest.skuCode() + " is having quantity: " + orderRequest.quantity() + " not enough!");
        }

    }
}
