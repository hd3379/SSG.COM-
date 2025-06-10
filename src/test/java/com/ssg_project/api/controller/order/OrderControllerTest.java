package com.ssg_project.api.controller.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg_project.api.controller.order.request.OrderCancleRequest;
import com.ssg_project.api.controller.order.request.OrderCreateRequest;
import com.ssg_project.api.controller.order.response.OrderCancelResponse;
import com.ssg_project.api.controller.order.response.OrderCreateResponse;
import com.ssg_project.api.controller.order.response.OrderGetInfoResponse;
import com.ssg_project.api.service.order.OrderService;
import com.ssg_project.api.service.order.dto.OrderItem;
import com.ssg_project.api.service.product.ProductBill;
import com.ssg_project.domain.product.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;


    @DisplayName("신규 주문을 등록한다.")
    @Test
    void createOrder() throws Exception {
        // given
        OrderCreateRequest request = OrderCreateRequest.builder()
                .orderItems(List.of(OrderItem.builder()
                        .productNumber("001")
                        .quantity(1)
                        .build())
                )
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/v1/orders/new")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
        ;
    }

    @DisplayName("신규 주문을 등록할 때 상품번호는 1개 이상이어야 한다.")
    @Test
    void createOrderWithEmptyProductNumbers() throws Exception {
        // given
        OrderCreateRequest request = OrderCreateRequest.builder()
                .orderItems(List.of())
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/v1/orders/new")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("상품 번호 및 숫자 리스트는 필수입니다."))
                .andExpect(jsonPath("$.data").isEmpty())
        ;
    }

    @DisplayName("주문에 있는 개별 상품을 골라 취소한다.")
    @Test
    void cancelOrder() throws Exception{
        LocalDateTime now = LocalDateTime.now();
        OrderCancleRequest request = OrderCancleRequest.builder()
                .orderNumber(1L)
                .productNumber("001")
                .build();

        OrderCancelResponse result = OrderCancelResponse.builder()
                .productBills(List.of(ProductBill.builder()
                        .productNumber("001").build()))
                .refundPrice(100)
                .totalPrice(1000)
                .build();

        when(orderService.cancelOrder(request,now)).thenReturn(result);

        // when // then
        mockMvc.perform(
                        post("/api/v1/orders/cancel")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @DisplayName("주문상품을 조회한다.")
    @Test
    void getOrderInfo() throws Exception {
        // given
        Long orderNumber = 1L;
        OrderGetInfoResponse result = OrderGetInfoResponse.builder()
                .totalPrice(1000)
                .productBills(
                        List.of(ProductBill.builder()
                                .productNumber("001").build())
                )
                .build();

        // when
        when(orderService.getOrderInfo(orderNumber)).thenReturn(result);

         // then
        mockMvc.perform(
                        get("/api/v1/orders/getInfo/{id}",1L)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
        ;
    }


}