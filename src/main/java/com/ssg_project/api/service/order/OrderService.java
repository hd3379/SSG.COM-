package com.ssg_project.api.service.order;

import com.ssg_project.api.controller.order.request.OrderCancleRequest;
import com.ssg_project.api.controller.order.response.OrderCancelResponse;
import com.ssg_project.api.controller.order.response.OrderCreateResponse;
import com.ssg_project.api.controller.order.response.OrderGetInfoResponse;
import com.ssg_project.api.service.order.dto.OrderItem;
import com.ssg_project.api.service.order.dto.request.OrderCreateServiceRequest;
import com.ssg_project.api.service.product.ProductBill;
import com.ssg_project.domain.order.Order;
import com.ssg_project.domain.order.OrderRepository;
import com.ssg_project.domain.orderproduct.OrderProduct;
import com.ssg_project.domain.orderproduct.OrderProductRepository;
import com.ssg_project.domain.product.Product;
import com.ssg_project.domain.product.ProductRepository;
import com.ssg_project.domain.sale.Sale;
import com.ssg_project.domain.sale.SaleRepository;
import com.ssg_project.domain.stock.Stock;
import com.ssg_project.domain.stock.StockRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final StockRepository stockRepository;
    private final SaleRepository saleRepository;
    private final OrderProductRepository orderProductRepository;

    /**
     * 재고 감소 -> 동시성 고민
     * optimistic lock / pessimistic lock
     */
    public OrderCreateResponse createOrder(OrderCreateServiceRequest request, LocalDateTime registeredDateTime) {
        List<String> productNumbers = getProductNumbers(request.getOrderItems());
        List<Product> products = findProductsBy(productNumbers);
        List<ProductBill> productBills = makeProductBills(products);

        int totalPrice = productBills.stream().mapToInt(ProductBill::getPrice).sum();
        deductStockQuantities(products);

        Order order = Order.create(products,totalPrice, registeredDateTime);
        Order savedOrder = orderRepository.save(order);
        return OrderCreateResponse.of(savedOrder,productBills);
    }

    public OrderCancelResponse cancelOrder(OrderCancleRequest request, LocalDateTime registeredDateTime) {
        Order order = findOrderBy(request.getOrderNumber());
        if(order == null) {
            throw new IllegalArgumentException("존재하지 않는 주문번호 입니다.");
        }
        String cancelProductNumber = request.getProductNumber();
        List<Product> cancelProducts = findByProductNumberFromOrder(cancelProductNumber);

        addStockQuantities(cancelProducts);

        List<ProductBill> cancelProductBills = makeProductBills(cancelProducts);
        List<Product> productsAfter = removeProductFromOrder(order,cancelProducts);
        List<ProductBill> productBillsAfter = makeProductBills(productsAfter);

        int refundPrice = cancelProductBills.stream().mapToInt(ProductBill::getPrice).sum();
        int afterPrice = productBillsAfter.stream().mapToInt(ProductBill::getPrice).sum();

        return OrderCancelResponse.builder()
                .productBills(cancelProductBills)
                .refundPrice(refundPrice)
                .totalPrice(afterPrice)
                .build();
    }

    public OrderGetInfoResponse getOrderInfo(Long orderId) {
        List<ProductBill> productBills = makeProductBills(orderId);

        return OrderGetInfoResponse.builder()
                .productBills(productBills)
                .totalPrice(productBills.stream().mapToInt(ProductBill::getPrice).sum())
                .build();
    }



    private List<Product> removeProductFromOrder(Order order, List<Product> cancelPoducts) {
        List<OrderProduct> orderProducts = order.getOrderProducts();
        for(Product product : cancelPoducts){
            orderProducts.removeIf(orderProduct ->
                    orderProduct.getProduct().getProductNumber().equals(product.getProductNumber())
            );
        }

        order.setOrderProducts(orderProducts);
        orderRepository.save(order);

        return order.getOrderProducts().stream().map(OrderProduct::getProduct).collect(Collectors.toList());
    }

    private List<Product> findByProductNumber(String productNumber) {
        return productRepository.findByProductNumber(productNumber);
    }



    private List<Product> findByProductNumberFromOrder(String productNumber) {
        Product product = productRepository.findTopByProductNumber(productNumber);
        if(product == null) {
            throw new IllegalArgumentException("존재하지 않는 상품ID 입니다.");
        }
        List<OrderProduct> orderProducts = orderProductRepository.findByProductId(product.getId());
        if(orderProducts.isEmpty()) {
            throw new IllegalArgumentException("이미 취소된 상품입니다.");
        }
        return orderProducts.stream().map(OrderProduct::getProduct).collect(Collectors.toList());
    }

    private Order findOrderBy(Long orderNumber) {
        return orderRepository.findById(orderNumber).orElse(null);
    }

    private List<ProductBill> makeProductBills(List<Product> products) {
        List<ProductBill> productBills = new ArrayList<>();
        List<String> saleProductNumbers = extractProductNumbers(products);
        Map<String, Sale> saleMap = createSaleMapBy(saleProductNumbers);

        for(Product product : products){
            int saledPrice = product.getPrice();
            if(saleMap.containsKey(product.getProductNumber())){
                saledPrice = saleMap.get(product.getProductNumber()).getSaledPrice(product.getPrice());
            }
            productBills.add(
                    ProductBill.builder()
                            .productNumber(product.getProductNumber())
                            .price(saledPrice)
                            .name(product.getName())
                            .build()
            );
        }
        return productBills;
    }

    private List<ProductBill> makeProductBills(Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if(order == null) {
            throw new IllegalArgumentException("존재하지 않는 주문 번호입니다.");
        }
        List<Product> products = order.getOrderProducts().stream()
                .map(OrderProduct::getProduct)
                .collect(Collectors.toList());

        List<ProductBill> productBills = new ArrayList<>();
        List<String> saleProductNumbers = extractProductNumbers(products);
        Map<String, Sale> saleMap = createSaleMapBy(saleProductNumbers);

        for(Product product : products){
            int saledPrice = product.getPrice();
            if(saleMap.containsKey(product.getProductNumber())){
                saledPrice = saleMap.get(product.getProductNumber()).getSaledPrice(product.getPrice());
            }

            boolean isNewProduct = true;
            for(int i=0; i<productBills.size(); i++){
                if(productBills.get(i).getProductNumber().equals(product.getProductNumber())){
                    productBills.get(i).addQuantity(saledPrice);
                    isNewProduct = false;
                    break;
                }
            }
            if(isNewProduct){
                productBills.add(
                        ProductBill.builder()
                                .productNumber(product.getProductNumber())
                                .price(saledPrice)
                                .name(product.getName())
                                .build()
                );
            }
        }


        return productBills;
    }

    private void addStockQuantities(List<Product> products) {
        List<String> stockProductNumbers = extractProductNumbers(products);

        Map<String, Stock> stockMap = createStockMapBy(stockProductNumbers);
        Map<String, Long> productCountingMap = createCountingMapBy(stockProductNumbers);

        for (String stockProductNumber : new HashSet<>(stockProductNumbers)) {
            Stock stock = stockMap.get(stockProductNumber);
            int quantity = productCountingMap.get(stockProductNumber).intValue();

            if(stock != null){
                stock.addQuantity(quantity);
            }
        }
    }

    private void deductStockQuantities(List<Product> products) {
        List<String> stockProductNumbers = extractProductNumbers(products);

        Map<String, Stock> stockMap = createStockMapBy(stockProductNumbers);
        Map<String, Long> productCountingMap = createCountingMapBy(stockProductNumbers);

        for (String stockProductNumber : new HashSet<>(stockProductNumbers)) {
            Stock stock = stockMap.get(stockProductNumber);
            int quantity = productCountingMap.get(stockProductNumber).intValue();

            if(stock != null){
                if (stock.isQuantityLessThan(quantity)) {
                    throw new IllegalArgumentException("재고가 부족한 상품이 있습니다.");
                }
                stock.deductQuantity(quantity);
            }
        }
    }

    private List<Product> findProductsBy(List<String> productNumbers) {
        List<Product> products = productRepository.findAllByProductNumberIn(productNumbers);
        Map<String, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getProductNumber, p -> p));

        return productNumbers.stream()
                .map(productMap::get)
                .collect(Collectors.toList());
    }

    private static List<String> extractProductNumbers(List<Product> products) {
        return products.stream()
                .map(Product::getProductNumber)
                .collect(Collectors.toList());
    }

    private Map<String, Stock> createStockMapBy(List<String> productNumbers) {
        List<Stock> stocks = stockRepository.findAllByProductNumberIn(productNumbers);
        return stocks.stream()
                .collect(Collectors.toMap(Stock::getProductNumber, s -> s));
    }

    private static Map<String, Long> createCountingMapBy(List<String> stockProductNumbers) {
        return stockProductNumbers.stream()
                .collect(Collectors.groupingBy(p -> p, Collectors.counting()));
    }

    private Map<String, Sale> createSaleMapBy(List<String> productNumbers) {
        List<Sale> sales = saleRepository.findAllByProductNumberIn(productNumbers);
        return sales.stream()
                .collect(Collectors.toMap(Sale::getProductNumber, s -> s));
    }



    public List<String> getProductNumbers(List<OrderItem> orderItems) {
        List<String> productNumbers = new ArrayList<>();
        for(OrderItem orderItem : orderItems){
            for(int i=0; i<orderItem.getQuantity(); i++){
                productNumbers.add(orderItem.getProductNumber());
            }
        }
        return productNumbers;
    }

}