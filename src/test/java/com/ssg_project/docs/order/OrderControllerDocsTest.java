package com.ssg_project.docs.order;

import com.ssg_project.api.controller.order.OrderController;
import com.ssg_project.api.controller.order.request.OrderCancleRequest;
import com.ssg_project.api.controller.order.request.OrderCreateRequest;
import com.ssg_project.api.controller.order.response.OrderCancelResponse;
import com.ssg_project.api.controller.order.response.OrderCreateResponse;
import com.ssg_project.api.controller.order.response.OrderGetInfoResponse;
import com.ssg_project.api.service.order.OrderService;
import com.ssg_project.api.service.order.dto.OrderItem;
import com.ssg_project.api.service.order.dto.request.OrderCreateServiceRequest;
import com.ssg_project.api.service.product.ProductBill;
import com.ssg_project.docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class OrderControllerDocsTest extends RestDocsSupport {

    private final OrderService orderService = mock(OrderService.class);

    @Override
    protected Object initController() {
        return new OrderController(orderService);
    }

    @DisplayName("주문 생성 API")
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

        given(orderService.createOrder(any(OrderCreateServiceRequest.class),any(LocalDateTime.class)))
                .willReturn(OrderCreateResponse.builder()
                        .id(1L)
                        .totalPrice(700)
                        .productBills(List.of(ProductBill.builder(
                                )
                                        .price(700)
                                        .name("이마트 생수")
                                        .productNumber("1000000001")
                                .build())
                        )
                        .build()
                );

        // when // then
        mockMvc.perform(
                        post("/api/v1/orders/new")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("order-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("orderItems").type(JsonFieldType.ARRAY)
                                        .description("상품 ID 및 수량 리스트"),
                                fieldWithPath("orderItems[].productNumber").type(JsonFieldType.STRING)
                                        .description("상품 ID"),
                                fieldWithPath("orderItems[].quantity").type(JsonFieldType.NUMBER)
                                        .description("수량")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메시지"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT)
                                        .description("응답 데이터"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                                        .description("주문번호"),
                                fieldWithPath("data.totalPrice").type(JsonFieldType.NUMBER)
                                        .description("주문 전체 금액"),
                                fieldWithPath("data.productBills").type(JsonFieldType.ARRAY)
                                        .description("각 주문 상품의 실구매금액"),
                                fieldWithPath("data.productBills[].productNumber").type(JsonFieldType.STRING)
                                        .description("상품 ID"),
                                fieldWithPath("data.productBills[].name").type(JsonFieldType.STRING)
                                        .description("상품 이름"),
                                fieldWithPath("data.productBills[].price").type(JsonFieldType.NUMBER)
                                        .description("상품 가격"),
                                fieldWithPath("data.productBills[].quantity").type(JsonFieldType.NUMBER)
                                        .description("상품 개수"),
                                fieldWithPath("data.registeredDateTime").type(JsonFieldType.NULL)
                                        .description("주문 등록 시간")
                        )
                        ));
    }

    @DisplayName("주문 상품 개별 취소 API")
    @Test
    void cancelProductInOrder() throws Exception {
        // given
        OrderCancleRequest request = OrderCancleRequest.builder()
                .orderNumber(1L)
                .productNumber("001")
                .build();

        given(orderService.cancelOrder(any(OrderCancleRequest.class),any(LocalDateTime.class)))
                .willReturn(OrderCancelResponse.builder()
                        .productBills(List.of(ProductBill.builder()
                                .price(700)
                                .name("이마트 생수")
                                .productNumber("1000000001")
                                .build()))
                        .refundPrice(700)
                        .totalPrice(0)
                        .build()
                );

        // when // then
        mockMvc.perform(
                        post("/api/v1/orders/cancel")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("order-cancel",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("orderNumber").type(JsonFieldType.NUMBER)
                                        .description("주문번호"),
                                fieldWithPath("productNumber").type(JsonFieldType.STRING)
                                        .description("취소할 상품 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메시지"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT)
                                        .description("응답 데이터"),
                                fieldWithPath("data.refundPrice").type(JsonFieldType.NUMBER)
                                        .description("주문번호"),
                                fieldWithPath("data.totalPrice").type(JsonFieldType.NUMBER)
                                        .description("주문 전체 금액"),
                                fieldWithPath("data.productBills").type(JsonFieldType.ARRAY)
                                        .description("각 주문 상품의 실구매금액"),
                                fieldWithPath("data.productBills[].productNumber").type(JsonFieldType.STRING)
                                        .description("상품 ID"),
                                fieldWithPath("data.productBills[].name").type(JsonFieldType.STRING)
                                        .description("상품 이름"),
                                fieldWithPath("data.productBills[].price").type(JsonFieldType.NUMBER)
                                        .description("상품 가격"),
                                fieldWithPath("data.productBills[].quantity").type(JsonFieldType.NUMBER)
                                        .description("상품 개수")
                        )
                ));
    }

    @DisplayName("주문 상품 조회 API")
    @Test
    void getOrderInfo() throws Exception {
        // given
        Long orderNumber = 1L;
        given(orderService.getOrderInfo(any(Long.class)))
                .willReturn(OrderGetInfoResponse.builder()
                        .totalPrice(1000)
                        .productBills(
                                List.of(ProductBill.builder()
                                        .price(700)
                                        .name("이마트 생수")
                                        .productNumber("1000000001")
                                        .build())
                        )
                        .build()
                );

        // when // then
        mockMvc.perform(
                    get("/api/v1/orders/getInfo/{id}",1L)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("get-order-info",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("주문번호")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메시지"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT)
                                        .description("응답 데이터"),
                                fieldWithPath("data.totalPrice").type(JsonFieldType.NUMBER)
                                        .description("주문 전체 금액"),
                                fieldWithPath("data.productBills").type(JsonFieldType.ARRAY)
                                        .description("각 주문 상품의 실구매금액"),
                                fieldWithPath("data.productBills[].productNumber").type(JsonFieldType.STRING)
                                        .description("상품 ID"),
                                fieldWithPath("data.productBills[].name").type(JsonFieldType.STRING)
                                        .description("상품 이름"),
                                fieldWithPath("data.productBills[].price").type(JsonFieldType.NUMBER)
                                        .description("상품 가격"),
                                fieldWithPath("data.productBills[].quantity").type(JsonFieldType.NUMBER)
                                        .description("상품 개수")
                        )
                ));
    }
}