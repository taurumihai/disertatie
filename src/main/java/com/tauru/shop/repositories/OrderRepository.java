package com.tauru.shop.repositories;

import com.tauru.shop.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {


    @Query("select orders from Order orders where orders.id = ?1")
    Order findOrderById(Long userId);
}
