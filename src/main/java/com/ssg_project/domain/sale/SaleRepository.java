package com.ssg_project.domain.sale;

import com.ssg_project.domain.stock.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findAllByProductNumberIn(List<String> productNumbers);
}
