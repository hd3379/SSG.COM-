package com.ssg_project.domain.order;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {

    INIT("주문생성"),
    CANCELED("주문취소"),
    PAYMENT_COMPLETED("결재완료"),
    PAYMENT_FAILED("결재실패"),
    RECEIVED("주문접수");

    private final String text;
}
