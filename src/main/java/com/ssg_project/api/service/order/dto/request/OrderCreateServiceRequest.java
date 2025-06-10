package com.ssg_project.api.service.order.dto.request;

import com.ssg_project.api.service.order.dto.OrderItem;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.Pair;

import java.util.List;

@Getter
@NoArgsConstructor
public class OrderCreateServiceRequest {

    private List<OrderItem> orderItems;

    @Builder
    private OrderCreateServiceRequest(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

}