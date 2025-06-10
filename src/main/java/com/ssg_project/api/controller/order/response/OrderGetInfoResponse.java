package com.ssg_project.api.controller.order.response;

import com.ssg_project.api.service.product.ProductBill;
import com.ssg_project.domain.order.Order;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class OrderGetInfoResponse {

    private int totalPrice;
    private List<ProductBill> productBills;

    @Builder
    public OrderGetInfoResponse(int totalPrice, List<ProductBill> productBills) {
        this.totalPrice = totalPrice;
        this.productBills = productBills;
    }
}
