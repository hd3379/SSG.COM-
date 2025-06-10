package com.ssg_project.domain.product;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @AfterEach
    void tearDown() {
        productRepository.deleteAllInBatch();
    }

    @DisplayName("가장 마지막으로 저장한 상품의 상품번호를 읽어온다.")
    @Test
    void findLatestProductNumber(){

        //given
        String targetProductNumber = "003";

        Product product1 = createProduct("001","생수",1000);
        Product product2 = createProduct("002","라면",1500);
        Product product3 = createProduct("003","바나나",2000);

        productRepository.saveAll(List.of(product1,product2,product3));

        //when
        String latestProductNumber = productRepository.findLatestProductNumber();

        //then
        assertThat(latestProductNumber).isEqualTo(targetProductNumber);
    }



    @DisplayName("상품번호 리스트로 상품들을 조회한다.")
    @Test
    void findAllByProductNumberIn(){

        //given
        Product product1 = createProduct("001","생수",1000);
        Product product2 = createProduct("002","라면",1500);
        Product product3 = createProduct("003","바나나",2000);

        productRepository.saveAll(List.of(product1,product2,product3));

        //when
        List<Product> products = productRepository.findAllByProductNumberIn(List.of("001","002"));

        //then
        assertThat(products).hasSize(2)
                .extracting("productNumber","name")
                .containsExactlyInAnyOrder(
                        tuple("001","생수"),
                        tuple("002","라면")
                );
    }


    @DisplayName("가장 마지막으로 저장한 상품의 상품번호를 읽어올떄, 상품이 하나도 없는 경우에는 null을 반환한다.")
    @Test
    void findLatestProductNumberWhenProductIsEmpty(){

        //when
        String latestProductNumber = productRepository.findLatestProductNumber();

        //then
        assertThat(latestProductNumber).isNull();
    }

    @DisplayName("productNumber 로 Product 정보를 받는다.")
    @Test
    void findTopByProductNumber(){
        //given
        String targetProductNumber = "003";
        Product product1 = createProduct("001","생수",1000);
        Product product2 = createProduct("002","라면",1500);
        Product product3 = createProduct("003","바나나",2000);

        productRepository.saveAll(List.of(product1,product2,product3));
        //when
        Product product = productRepository.findTopByProductNumber(targetProductNumber);

        //then
        assertThat(product.getProductNumber()).isEqualTo(targetProductNumber);
    }


    private Product createProduct(String productNumber, String name, int price) {
        return Product.builder()
                .productNumber(productNumber)
                .name(name)
                .price(price)
                .build();
    }
}
