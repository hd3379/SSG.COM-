package com.ssg_project.api.controller.order;

import com.ssg_project.api.ApiResponse;
import com.ssg_project.api.controller.order.request.OrderCancleRequest;
import com.ssg_project.api.controller.order.request.OrderCreateRequest;
import com.ssg_project.api.controller.order.response.OrderCancelResponse;
import com.ssg_project.api.controller.order.response.OrderCreateResponse;
import com.ssg_project.api.controller.order.response.OrderGetInfoResponse;
import com.ssg_project.api.service.order.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestController
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/api/v1/orders/new")
    public ApiResponse<OrderCreateResponse> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        LocalDateTime registeredDateTime = LocalDateTime.now();
        return ApiResponse.ok(orderService.createOrder(request.toServiceRequest(), registeredDateTime));
    }

    @PostMapping("/api/v1/orders/cancel")
    public ApiResponse<OrderCancelResponse> cancelOrder(@Valid @RequestBody OrderCancleRequest request) {
        LocalDateTime registeredDateTime = LocalDateTime.now();
        return ApiResponse.ok(orderService.cancelOrder(request.toServiceRequest(), registeredDateTime));
    }

    @GetMapping("/api/v1/orders/getInfo/{id}")
    public ApiResponse<OrderGetInfoResponse> getOrderInfo(@PathVariable("id") Long orderId) {
        return ApiResponse.ok(orderService.getOrderInfo(orderId));
    }

}

