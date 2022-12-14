package jpabook.jpashop.repository.order.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderFlatDto {
    private Long orderId;
    private String name;
    private LocalDateTime orderDate; //주문시간 private Address address;
    private Address address;
    private OrderStatus orderStatus;
    private String itemName;//상품 명 private int orderPrice; //주문 가격 private int count; //주문 수량
    private int orderPrice;
    private int count;

    public OrderFlatDto(Long orderId, String name, LocalDateTime orderDate,
                        OrderStatus orderStatus, Address address, String itemName, int orderPrice, int
                                count) {
        this.orderId = orderId;
        this.name = name;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;
        this.itemName = itemName;
        this.orderPrice = orderPrice;
        this.count = count;
    }


    /*
    하나의 DB에 order , OrderItem의 필드들을 넣어주는 Dto 생성

    * */
}
