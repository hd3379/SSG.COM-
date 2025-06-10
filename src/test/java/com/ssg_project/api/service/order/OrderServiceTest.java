package com.ssg_project.api.service.order;

import com.ssg_project.api.controller.order.request.OrderCancleRequest;
import com.ssg_project.api.controller.order.request.OrderCreateRequest;
import com.ssg_project.api.controller.order.response.OrderCancelResponse;
import com.ssg_project.api.controller.order.response.OrderCreateResponse;
import com.ssg_project.api.controller.order.response.OrderGetInfoResponse;
import com.ssg_project.api.service.order.dto.OrderItem;
import com.ssg_project.domain.order.OrderRepository;
import com.ssg_project.domain.orderproduct.OrderProductRepository;
import com.ssg_project.domain.product.Product;
import com.ssg_project.domain.product.ProductRepository;
import com.ssg_project.domain.sale.Sale;
import com.ssg_project.domain.sale.SaleRepository;
import com.ssg_project.domain.stock.Stock;
import com.ssg_project.domain.stock.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class OrderServiceTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private OrderService orderService;

    @AfterEach
    void tearDown() {
        orderProductRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        orderRepository.deleteAllInBatch();
        stockRepository.deleteAllInBatch();
        saleRepository.deleteAllInBatch();
    }

    @DisplayName("주문상품 리스트를 받아 주문을 생성한다.")
    @Test
    void createOrder() {
        // given
        LocalDateTime registeredDateTime = LocalDateTime.now();

        Product product1 = createProduct("001", 1000);
        Product product2 = createProduct("002", 3000);
        Product product3 = createProduct("003", 5000);
        productRepository.saveAll(List.of(product1, product2, product3));

        OrderCreateRequest request = OrderCreateRequest.builder()
                .orderItems(List.of(
                        OrderItem.builder()
                        .productNumber("001")
                        .quantity(1)
                        .build(),
                        OrderItem.builder()
                                .productNumber("002")
                                .quantity(1)
                                .build()
                ))
                .build();

        // when
        OrderCreateResponse orderCreateResponse = orderService.createOrder(request.toServiceRequest(), registeredDateTime);

        // then
        assertThat(orderCreateResponse.getId()).isNotNull();
        assertThat(orderCreateResponse)
                .extracting("registeredDateTime", "totalPrice")
                .contains(registeredDateTime, 4000);
        assertThat(orderCreateResponse.getProductBills()).hasSize(2)
                .extracting("productNumber", "price")
                .containsExactlyInAnyOrder(
                        tuple("001", 1000),
                        tuple("002", 3000)
                );
    }

    @DisplayName("중복되는 상품번호 리스트로 주문을 생성할 수 있다.")
    @Test
    void createOrderWithDuplicateProductNumbers() {
        // given
        LocalDateTime registeredDateTime = LocalDateTime.now();

        Product product1 = createProduct("001", 1000);
        Product product2 = createProduct("002", 3000);
        Product product3 = createProduct("003", 5000);
        productRepository.saveAll(List.of(product1, product2, product3));

        OrderCreateRequest request = OrderCreateRequest.builder()
                .orderItems(List.of(
                        OrderItem.builder()
                                .productNumber("001")
                                .quantity(1)
                                .build(),
                        OrderItem.builder()
                                .productNumber("001")
                                .quantity(1)
                                .build()
                ))
                .build();

        // when
        OrderCreateResponse orderCreateResponse = orderService.createOrder(request.toServiceRequest(), registeredDateTime);

        // then
        assertThat(orderCreateResponse.getId()).isNotNull();
        assertThat(orderCreateResponse)
                .extracting("registeredDateTime", "totalPrice")
                .contains(registeredDateTime, 2000);
        assertThat(orderCreateResponse.getProductBills()).hasSize(2)
                .extracting("productNumber", "price")
                .containsExactlyInAnyOrder(
                        tuple("001", 1000),
                        tuple("001", 1000)
                );
    }

    @DisplayName("재고와 관련된 상품이 포함되어 있는 주문번호 리스트를 받아 주문을 생성한다.")
    @Test
    void createOrderWithStock() {
        // given
        LocalDateTime registeredDateTime = LocalDateTime.now();

        Product product1 = createProduct("001", 1000);
        Product product2 = createProduct("002", 3000);
        Product product3 = createProduct("003", 5000);
        productRepository.saveAll(List.of(product1, product2, product3));

        Stock stock1 = Stock.create("001", 2);
        Stock stock2 = Stock.create("002", 2);
        stockRepository.saveAll(List.of(stock1, stock2));

        OrderCreateRequest request = OrderCreateRequest.builder()
                .orderItems(List.of(
                        OrderItem.builder()
                                .productNumber("001")
                                .quantity(2)
                                .build(),
                        OrderItem.builder()
                                .productNumber("002")
                                .quantity(1)
                                .build(),

                        OrderItem.builder()
                                .productNumber("003")
                                .quantity(1)
                                .build()
                ))
                .build();

        // when
        OrderCreateResponse orderCreateResponse = orderService.createOrder(request.toServiceRequest(), registeredDateTime);

        // then
        assertThat(orderCreateResponse.getId()).isNotNull();
        assertThat(orderCreateResponse)
                .extracting("registeredDateTime", "totalPrice")
                .contains(registeredDateTime, 10000);
        assertThat(orderCreateResponse.getProductBills()).hasSize(4)
                .extracting("productNumber", "price")
                .containsExactlyInAnyOrder(
                        tuple("001", 1000),
                        tuple("001", 1000),
                        tuple("002", 3000),
                        tuple("003", 5000)
                );

        List<Stock> stocks = stockRepository.findAll();
        assertThat(stocks).hasSize(2)
                .extracting("productNumber", "quantity")
                .containsExactlyInAnyOrder(
                        tuple("001", 0),
                        tuple("002", 1)
                );
    }

    @DisplayName("재고가 부족한 상품으로 주문을 생성하려는 경우 예외가 발생한다.")
    @Test
    void createOrderWithNoStock() {
        // given
        LocalDateTime registeredDateTime = LocalDateTime.now();

        Product product1 = createProduct("001", 1000);
        Product product2 = createProduct("002", 3000);
        Product product3 = createProduct("003", 5000);
        productRepository.saveAll(List.of(product1, product2, product3));

        Stock stock1 = Stock.create("001", 2);
        Stock stock2 = Stock.create("002", 2);
        stock1.deductQuantity(1);

        stockRepository.saveAll(List.of(stock1, stock2));

        OrderCreateRequest request = OrderCreateRequest.builder()
                .orderItems(List.of(
                        OrderItem.builder()
                                .productNumber("001")
                                .quantity(2)
                                .build(),
                        OrderItem.builder()
                                .productNumber("002")
                                .quantity(1)
                                .build(),

                        OrderItem.builder()
                                .productNumber("003")
                                .quantity(1)
                                .build()
                ))
                .build();

        // when // then
        assertThatThrownBy(() -> orderService.createOrder(request.toServiceRequest(), registeredDateTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고가 부족한 상품이 있습니다.");
    }


    @DisplayName("할인이 적용된 상품이 포함되어 있는 주문번호 리스트를 받아 주문을 생성한다.")
    @Test
    void createOrderWithSale() {
        // given
        LocalDateTime registeredDateTime = LocalDateTime.now();

        Product product1 = createProduct("001", 1000);
        Product product2 = createProduct("002", 3000);
        Product product3 = createProduct("003", 5000);
        productRepository.saveAll(List.of(product1, product2, product3));

        Sale sale1 = Sale.create("001", 500);
        Sale sale2 = Sale.create("002", 1000);
        saleRepository.saveAll(List.of(sale1, sale2));

        OrderCreateRequest request = OrderCreateRequest.builder()
                .orderItems(List.of(
                        OrderItem.builder()
                                .productNumber("001")
                                .quantity(2)
                                .build(),
                        OrderItem.builder()
                                .productNumber("002")
                                .quantity(1)
                                .build(),

                        OrderItem.builder()
                                .productNumber("003")
                                .quantity(1)
                                .build()
                ))
                .build();

        int totalPriceForCompare = (1000 - 500) + (1000 - 500) + (3000 - 1000) + 5000;

        // when
        OrderCreateResponse orderCreateResponse = orderService.createOrder(request.toServiceRequest(), registeredDateTime);

        // then
        assertThat(orderCreateResponse.getId()).isNotNull();
        assertThat(orderCreateResponse)
                .extracting("registeredDateTime", "totalPrice")
                .contains(registeredDateTime, totalPriceForCompare);
        assertThat(orderCreateResponse.getProductBills()).hasSize(4)
                .extracting("productNumber", "price")
                .containsExactlyInAnyOrder(
                        tuple("001", 500),
                        tuple("001", 500),
                        tuple("002", 2000),
                        tuple("003", 5000)
                );
    }

    @DisplayName("할인이 적용됐고 재고도 적용된 상품이 포함되어 있는 주문번호 리스트를 받아 주문을 생성한다.")
    @Test
    void createOrderWithSaleAndStock() {
        // given
        LocalDateTime registeredDateTime = LocalDateTime.now();

        Product product1 = createProduct("001", 1000);
        Product product2 = createProduct("002", 3000);
        Product product3 = createProduct("003", 5000);
        productRepository.saveAll(List.of(product1, product2, product3));

        Stock stock1 = Stock.create("001", 2);
        Stock stock2 = Stock.create("002", 2);
        stockRepository.saveAll(List.of(stock1, stock2));

        Sale sale1 = Sale.create("001", 500);
        Sale sale2 = Sale.create("002", 1000);
        saleRepository.saveAll(List.of(sale1, sale2));

        OrderCreateRequest request = OrderCreateRequest.builder()
                .orderItems(List.of(
                        OrderItem.builder()
                                .productNumber("001")
                                .quantity(2)
                                .build(),
                        OrderItem.builder()
                                .productNumber("002")
                                .quantity(1)
                                .build(),

                        OrderItem.builder()
                                .productNumber("003")
                                .quantity(1)
                                .build()
                ))
                .build();

        int totalPriceForCompare = (1000 - 500) + (1000 - 500) + (3000 - 1000) + 5000;

        // when
        OrderCreateResponse orderCreateResponse = orderService.createOrder(request.toServiceRequest(), registeredDateTime);

        // then
        assertThat(orderCreateResponse.getId()).isNotNull();
        assertThat(orderCreateResponse)
                .extracting("registeredDateTime", "totalPrice")
                .contains(registeredDateTime, totalPriceForCompare);
        assertThat(orderCreateResponse.getProductBills()).hasSize(4)
                .extracting("productNumber", "price")
                .containsExactlyInAnyOrder(
                        tuple("001", 500),
                        tuple("001", 500),
                        tuple("002", 2000),
                        tuple("003", 5000)
                );

        List<Stock> stocks = stockRepository.findAll();
        assertThat(stocks).hasSize(2)
                .extracting("productNumber", "quantity")
                .containsExactlyInAnyOrder(
                        tuple("001", 0),
                        tuple("002", 1)
                );
    }

    @DisplayName("주문된 내역에서 상품 1개를 취소한다.")
    @Test
    void cancelProductFromOrder() {
        // given
        OrderCreateResponse orderCreateResponse = createOrderSample();
        OrderCancleRequest cancelRequest = OrderCancleRequest.builder()
                .orderNumber(orderCreateResponse.getId())
                .productNumber("001")
                .build();

        int totalPriceForCompare = (3000 - 1000) + 5000;
        int refundPriceCompare = (1000 - 500) + (1000 - 500);

        LocalDateTime registeredDateTime = LocalDateTime.now();
        // when
        OrderCancelResponse orderCancelResponse = orderService.cancelOrder(cancelRequest, registeredDateTime);

        // then
        assertThat(orderCancelResponse)
                .extracting("refundPrice", "totalPrice")
                .contains(refundPriceCompare, totalPriceForCompare);
        assertThat(orderCancelResponse.getProductBills()).hasSize(2)
                .extracting("productNumber", "price")
                .containsExactlyInAnyOrder(
                        tuple("001", 500),
                        tuple("001", 500)
                );

        List<Stock> stocks = stockRepository.findAll();
        assertThat(stocks).hasSize(2)
                .extracting("productNumber", "quantity")
                .containsExactlyInAnyOrder(
                        tuple("001", 2),
                        tuple("002", 1)
                );
    }

    @DisplayName("없는 상품번호를 취소했을경우 에러를 뱉는다.")
    @Test
    void cancelOrderWithWorgProductNumber() {
        // given
        OrderCreateResponse orderCreateResponse = createOrderSample(); //샘플 데이터 주석으로 설명
        OrderCancleRequest cancleRequest = OrderCancleRequest.builder()
                .orderNumber(orderCreateResponse.getId())
                .productNumber("004")
                .build();

        int totalPriceForCompare = (3000 - 1000) + 5000;
        int refundPriceCompare = (1000 - 500) + (1000 - 500);

        LocalDateTime registeredDateTime = LocalDateTime.now();
        // when //then
        assertThatThrownBy(() -> orderService.cancelOrder(cancleRequest, registeredDateTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 상품ID 입니다.");
    }

    @DisplayName("이미 취소된 상품번호를 취소했을경우 에러를 뱉는다.")
    @Test
    void cancelOrderWithAlreadyCanceledProductNumber() {
        // given
        OrderCreateResponse orderCreateResponse = createOrderSample(); //샘플 데이터 주석으로 설명

        OrderCancleRequest cancleRequest = OrderCancleRequest.builder()
                .orderNumber(orderCreateResponse.getId())
                .productNumber("003")
                .build();

        int totalPriceForCompare = (3000 - 1000) + 5000;
        int refundPriceCompare = (1000 - 500) + (1000 - 500);
        LocalDateTime registeredDateTime = LocalDateTime.now();
        orderService.cancelOrder(cancleRequest, registeredDateTime);

        // when //then
        assertThatThrownBy(() -> orderService.cancelOrder(cancleRequest, registeredDateTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 취소된 상품입니다.");
    }

    @DisplayName("주문번호로 주문을 조회한다.")
    @Test
    void getOrderInfo(){
        //given
        OrderCreateResponse orderCreateResponse = createOrderSample();//샘플 데이터 주석으로 설명
        Long orderId = orderCreateResponse.getId();

        //when
        OrderGetInfoResponse orderGetInfoResponse = orderService.getOrderInfo(orderId);

        //then
        assertThat(orderGetInfoResponse).isNotNull();
        assertThat(orderGetInfoResponse.getTotalPrice()).isEqualTo(orderCreateResponse.getTotalPrice());
        assertThat(orderGetInfoResponse.getProductBills()).hasSize(3)
                .extracting("productNumber", "price", "quantity")
                .containsExactlyInAnyOrder(
                        tuple("001", 1000,2),
                        tuple("002", 2000,1),
                        tuple("003", 5000,1)
                );
    }

    @DisplayName("존재하지 않는 주문번호로 주문을 조회한다.")
    @Test
    void getOrderInfoWithWrongOrderNumber(){
        //given
        OrderCreateResponse orderCreateResponse = createOrderSample();//샘플 데이터 주석으로 설명
        Long orderId = Long.MAX_VALUE;

        //when //then
        assertThatThrownBy(() -> orderService.getOrderInfo(orderId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 주문 번호입니다.");

    }



    /**
     * <pre>001 (1000원), 002 (3000원), 003 (5000원)상품이 존재
     * 001, 002 상품 재고가2개
     * 001은 500원, 002는 1000원 할인중
     *
     * 주문내역:
     * 001(500원) 2개, 002(2000원) 1개, 003(5000원) 1개 주문함
     * 총 금액은 8000원
     *</pre>
     * */
    private OrderCreateResponse createOrderSample(){
        LocalDateTime registeredDateTime = LocalDateTime.now();
        Product product1 = createProduct("001", 1000);
        Product product2 = createProduct("002", 3000);
        Product product3 = createProduct("003", 5000);
        productRepository.saveAll(List.of(product1, product2, product3));

        Stock stock1 = Stock.create("001", 2);
        Stock stock2 = Stock.create("002", 2);
        stockRepository.saveAll(List.of(stock1, stock2));

        Sale sale1 = Sale.create("001", 500);
        Sale sale2 = Sale.create("002", 1000);
        saleRepository.saveAll(List.of(sale1, sale2));

        OrderCreateRequest request = OrderCreateRequest.builder()
                .orderItems(List.of(
                        OrderItem.builder()
                                .productNumber("001")
                                .quantity(2)
                                .build(),
                        OrderItem.builder()
                                .productNumber("002")
                                .quantity(1)
                                .build(),

                        OrderItem.builder()
                                .productNumber("003")
                                .quantity(1)
                                .build()
                ))
                .build();

        return orderService.createOrder(request.toServiceRequest(), registeredDateTime);
    }


    private Product createProduct(String productNumber, int price) {
        return Product.builder()
                .productNumber(productNumber)
                .price(price)
                .name("메뉴 이름")
                .build();
    }

}