package com.ssg_project.api.service.product;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductBill {
    private String productNumber;
    private String name;
    private int price;
    private int quantity;

    @Builder
    public ProductBill(String productNumber, String name, int price) {
        this.productNumber = productNumber;
        this.name = name;
        this.price = price;
        this.quantity = 1;
    }

    @Builder
    public ProductBill(String productNumber, String name, int price, int quantity) {
        this.productNumber = productNumber;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public void addQuantity(int saledPrice) {
        this.quantity = this.quantity + 1;
        this.price = this.price + saledPrice;
    }
}
