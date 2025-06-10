package com.ssg_project.domain.product;

import com.ssg_project.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productNumber;

    private String name;

    private int price;

    @Builder
    public Product(String productNumber, String name, int price) {
        this.productNumber = productNumber;
        this.name = name;
        this.price = price;
    }

}
