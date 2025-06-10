package com.ssg_project.api.controller.order.response;

import com.ssg_project.api.service.product.ProductBill;
import com.ssg_project.domain.order.Order;
import com.ssg_project.domain.orderproduct.OrderProduct;
import com.ssg_project.domain.product.Product;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class OrderCreateResponse {

    private Long id;
    private int totalPrice;
    private List<ProductBill> productBills;
    private LocalDateTime registeredDateTime;

    @Builder
    public OrderCreateResponse(Long id, int totalPrice, LocalDateTime registeredDateTime, List<ProductBill> productBills) {
        this.id = id;
        this.totalPrice = totalPrice;
        this.registeredDateTime = registeredDateTime;
        this.productBills = productBills;
    }

    public static OrderCreateResponse of(Order order, List<ProductBill> productBills) {
        return OrderCreateResponse.builder()
                .id(order.getId())
                .totalPrice(order.getTotalPrice())
                .registeredDateTime(order.getRegisteredDateTime())
                .productBills(productBills)
                .build();
    }
}
