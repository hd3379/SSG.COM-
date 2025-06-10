package com.ssg_project.api.controller.order.response;

import com.ssg_project.api.service.product.ProductBill;
import com.ssg_project.domain.product.Product;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class OrderCancelResponse {
    List<ProductBill> productBills;
    int refundPrice;
    int totalPrice;

    @Builder
    public OrderCancelResponse(List<ProductBill> productBills, int refundPrice, int totalPrice) {
        this.productBills = productBills;
        this.refundPrice = refundPrice;
        this.totalPrice = totalPrice;
    }
}
