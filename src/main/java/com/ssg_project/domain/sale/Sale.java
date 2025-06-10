package com.ssg_project.domain.sale;

import com.ssg_project.domain.stock.Stock;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productNumber;

    private int salePrice;

    @Builder
    public Sale(String productNumber, int salePrice) {
        this.productNumber = productNumber;
        this.salePrice = salePrice;
    }

    public static Sale create(String productNumber, int salePrice) {
        return Sale.builder()
                .productNumber(productNumber)
                .salePrice(salePrice)
                .build();
    }

    public int getSaledPrice(int price) {
        int paid = price - this.salePrice;
        if( paid < 0) {
            paid = 0;
        }

        return paid;
    }
}
