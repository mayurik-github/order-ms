package com.order.ms.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.order.ms.dto.CustomerOrder;
import com.order.ms.dto.OrderEvent;
import com.order.ms.entity.Order;
import com.order.ms.entity.OrderRepository;

@RestController
@RequestMapping("/api")
public class OrderController {

	@Autowired
	private OrderRepository repository;

	@Autowired
	private KafkaTemplate<String, OrderEvent> kafkaTemplate;
	
	private Logger logger = LoggerFactory.getLogger(OrderController.class);

	@PostMapping("/orders")
	public void createOrder(@RequestBody CustomerOrder customerOrder) {
		logger.debug("Create order for item {}", customerOrder.getItem());
		Order order = new Order();

		try {
			order.setAmount(customerOrder.getAmount());
			order.setItem(customerOrder.getItem());
			order.setQuantity(customerOrder.getQuantity());
			order.setStatus("CREATED");
			order = repository.save(order);

			customerOrder.setOrderId(order.getId());

			OrderEvent event = new OrderEvent();
			event.setOrder(customerOrder);
			event.setType("ORDER_CREATED");
			kafkaTemplate.send("new-orders", event);
			logger.info("Order {} created with status {} ", customerOrder.getOrderId(), order.getStatus());
		} catch (Exception e) {
			logger.error("Failed order creation for order number {} with error {} ", customerOrder.getOrderId(), e.getMessage());
			order.setStatus("FAILED");
			repository.save(order);
		}
		
	}
}
