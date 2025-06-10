package com.ssg_project.api.service.order;

import com.ssg_project.domain.order.Order;
import com.ssg_project.domain.order.OrderStatus;
import com.ssg_project.domain.product.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {


    @DisplayName("주문 생성 시 주문 상태는 INIT이다.")
    @Test
    void init(){
        //given
        List<Product> products = List.of(
                createProduct("001",1000),
                createProduct("002",2000)
        );
        int totalPrice = 3000;

        //when
        Order order = Order.create(products,totalPrice, LocalDateTime.now());

        //then
        assertThat(order.getOrderStatus()).isEqualByComparingTo(OrderStatus.INIT);
    }

    @DisplayName("주문 생성 시 주문 등록 시간을 기록한다.")
    @Test
    void registeredDateTime(){
        //given
        LocalDateTime registeredDateTime = LocalDateTime.now();

        List<Product> products = List.of(
                createProduct("001",1000),
                createProduct("002",2000)
        );
        int totalPrice = 3000;

        //when

        Order order = Order.create(products,totalPrice, registeredDateTime);

        //then
        assertThat(order.getRegisteredDateTime()).isEqualTo(registeredDateTime);
    }


    private Product createProduct(String productNumber, int price){
        return Product.builder()
                .productNumber(productNumber)
                .price(price)
                .name("메뉴이름")
                .build();
    }

}