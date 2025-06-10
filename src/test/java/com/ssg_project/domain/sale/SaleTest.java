package com.ssg_project.domain.sale;

import com.ssg_project.domain.stock.Stock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class SaleTest {

    @DisplayName("가격을 주어진 할인가격 만큼 낮출 수 있다.")
    @Test
    void deductPrice(){
        //given
        Sale sale = Sale.create("001", 100);
        int price = 1000;

        //when
        int saledPrice = sale.getSaledPrice(price);

        //then
        assertEquals(900, saledPrice);
    }

    @DisplayName("가격이 주어진 할인가격 보다 낮으면 가격은 0원이 된다.")
    @Test
    void deductPrice2(){
        //given
        Sale sale = Sale.create("001", 1200);
        int price = 1000;

        //when
        int saledPrice = sale.getSaledPrice(price);

        //then
        assertEquals(0, saledPrice);
    }
}
