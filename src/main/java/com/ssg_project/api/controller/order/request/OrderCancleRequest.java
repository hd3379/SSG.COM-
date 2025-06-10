package com.ssg_project.api.controller.order.request;

import com.ssg_project.domain.order.Order;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OrderCancleRequest {

    @NotNull(message = "주문 번호는 필수입니다.")
    private Long orderNumber;

    @NotBlank(message = "상품 번호는 필수입니다.")
    private String productNumber;

    @Builder
    public OrderCancleRequest(Long orderNumber, String productNumber) {
        this.orderNumber = orderNumber;
        this.productNumber = productNumber;
    }

    public OrderCancleRequest toServiceRequest() {
        return OrderCancleRequest.builder()
                .orderNumber(orderNumber)
                .productNumber(productNumber)
                .build();
    }
}
