package com.eugene.order_service.repository;

import com.eugene.order_service.entity.OrderLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderLineRepository extends JpaRepository<OrderLine, Integer> {
    // The orderId in the method findByOrderId refers to the order_id column in the OrderLine entity, which is a reference to the Order entity.
    List<OrderLine> findAllByOrderId(Integer orderId);
}
