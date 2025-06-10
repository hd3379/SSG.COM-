package com.ssg_project.api.controller.order.request;

import com.ssg_project.api.service.order.dto.OrderItem;
import com.ssg_project.api.service.order.dto.request.OrderCreateServiceRequest;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.util.Pair;

import java.util.List;

@Getter
@NoArgsConstructor
public class OrderCreateRequest {

    @NotEmpty(message = "상품 번호 및 숫자 리스트는 필수입니다.")
    private List<OrderItem> orderItems;

    @Builder
    public OrderCreateRequest(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public OrderCreateServiceRequest toServiceRequest() {
        return OrderCreateServiceRequest.builder()
                .orderItems(orderItems)
                .build();
    }
}
