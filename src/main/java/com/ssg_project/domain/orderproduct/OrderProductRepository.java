package com.ssg_project.domain.orderproduct;

import com.ssg_project.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
    List<OrderProduct> findByProductId(Long productId);
}
