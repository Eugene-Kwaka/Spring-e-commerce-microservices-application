package com.eugene.order_service.entity;

import com.eugene.order_service.dto.ProductPurchaseDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static jakarta.persistence.EnumType.STRING;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="customer_order")
/**
 * This annotation is used in JPA (Java Persistence API) to specify that the Order entity should be monitored by the AuditingEntityListener.
 * The Listener class is part of Spring Data JPA and is used to automatically populate auditing fields such as createdAt and lastModifiedAt with the current timestamp when the entity is created or updated.
 * This auditing is useful for tracking the creation and modification times of entities without having to manually set the fields in the app's code.*/
@EntityListeners(AuditingEntityListener.class )
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String reference;

    private BigDecimal totalAmount;

    /**
     * Specifies that the paymentMethod field in the Order entity should be persisted as a String in the database.
     * The enum values(PAYPAL, CREDIT_CARD e.t.c) enum values will be stored as their corresponding String names in the DB, rather than original Integer values.
     * */
    @Enumerated(STRING)
    private PaymentMethod paymentMethod;

    /**
     * The id is string because the customer-service uses a NoSQL database.
     * The customerId field is used to reference the customer from the customer-service without creating a direct foreign key relationship.
     * This allows the order-service to store customer information with tightly coupling the two databases.
     * By using the customerId as a simple string field, you can maintain a reference to the customer while keeping the databases independent.*/
    private String customerId;


    /**
     * Defines a OneToMany relationship between the Order and OrderLine entities.
     * This shows that the Order entity can have many OrderLine entities associated with it.
     * The mappedBy="order" indicates that the OrderLine entity has a field named order that owns the relationship.
     * */
    @OneToMany(mappedBy="order")
    private List<OrderLine> orderLines;

    private List<ProductPurchaseDTO> productPurchasesDTO;

    /**
     * Automatically populates the annotated field with the timestamp of when the entity was first created.
     * */
    @CreatedDate
    @Column(updatable=false, nullable=false)
    private LocalDateTime createdAt;

    /**
     * Automatically populates the annotated field with the timestamp of when the entity was last modified.
     * */
    @LastModifiedDate
    @Column(updatable=false, nullable=false)
    private LocalDateTime lastModifiedAt;


}
