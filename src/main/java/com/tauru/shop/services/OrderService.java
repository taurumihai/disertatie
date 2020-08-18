package com.tauru.shop.services;


import com.tauru.shop.entities.Order;
import com.tauru.shop.entities.Product;
import com.tauru.shop.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public void saveOrder(Order order) {

        if (order != null) {

            orderRepository.save(order);
        }
    }

    public List<Order> getAllUnprocessedOrders() {

        List<Order> unprocessedOrders = new ArrayList<>();

        for (Order order : orderRepository.findAll()) {
            if (!order.getOrderIsProcessed()) unprocessedOrders.add(order);
        }

        return unprocessedOrders;
    }

    public Order findOrderById(Long orderId) {

        return orderRepository.findOrderById(orderId);
    }

    public List<Order> findAllOrders() {

        return orderRepository.findAll();
    }

}
