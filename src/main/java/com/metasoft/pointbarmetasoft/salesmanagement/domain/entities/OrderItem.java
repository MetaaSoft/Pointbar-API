package com.metasoft.pointbarmetasoft.salesmanagement.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.metasoft.pointbarmetasoft.beveragemanagement.domain.entities.Beverage;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beverage_id", nullable = false)
    private Beverage beverage;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Boolean delivered;

    public OrderItem(Order order, Beverage beverage, Integer quantity, Boolean delivered) {
        this.order = order;
        this.beverage = beverage;
        this.quantity = quantity;
        this.delivered = delivered;
    }
}
