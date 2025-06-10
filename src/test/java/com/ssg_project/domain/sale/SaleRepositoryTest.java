package com.ssg_project.domain.sale;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
public class SaleRepositoryTest {

    @Autowired
    private SaleRepository saleRepository;

    @DisplayName("상품번호 리스트로 할인금액을 조회한다.")
    @Test
    void findAllByProductNumberIn(){

        //given
        Sale sale1 = Sale.create("001",100);
        Sale sale2 = Sale.create("002",200);
        Sale sale3 = Sale.create("003",500);
        saleRepository.saveAll(List.of(sale1,sale2,sale3));

        //when
        List<Sale> sales = saleRepository.findAllByProductNumberIn(List.of("001","002"));

        //then
        assertThat(sales).hasSize(2)
                .extracting("productNumber","salePrice")
                .containsExactlyInAnyOrder(
                        tuple("001",100),
                        tuple("002",200)
                );
    }
}
