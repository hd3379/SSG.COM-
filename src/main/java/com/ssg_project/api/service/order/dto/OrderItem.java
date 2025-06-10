package com.ssg_project.api.service.order.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {
    private String productNumber;
    private int quantity;

    @Builder
    public OrderItem(String productNumber, int quantity) {
        this.productNumber = productNumber;
        this.quantity = quantity;
    }
}
