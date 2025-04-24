package com.eugene.order_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class OrderLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne // One order can have multiple orderlines (e.g one fior each product in the order.)
    @JoinColumn(name="order_id")
    private Order order;

    /**
     * In each orderLine, we need to see a direct reference to a product.
     * */
    private Integer productId;

    private Double quantity;
}
