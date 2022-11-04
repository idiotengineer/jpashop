package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderSimpleQueryDto {
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;

    public OrderSimpleQueryDto(Order order) {
        orderId = order.getId();
        name = order.getMember().getName(); //Member LAZY 초기화호출
        orderDate = order.getOrderDate();
        orderStatus = order.getStatus();
        address = order.getDelivery().getAddress(); //Delivery Lazy 초기화호출
    }

        /*
        쿼리 생성 개수가 너무 많다!
        1. Order
        2. Member
        3. Delivery
        -> 이것을 반복함... ( 3번,6번,9번,,,,)
            N + 1의 문제가 생김.
            -> N(2) =  1 + Member(2) + Delivery(2)

        Eager로 하면 어떻게 되는가?
        1. Order를 가져옴
        2. Order의 Eager(member)를 또 가져옴.
        3. Order의 Eager(delivery)를 또 가져옴.
        -> 개선안됨. 만약 Delivery, Member가 같으면 줄어들긴 하지만 최악에는 N+1로 아주 안좋다...
        * */



        /*
        public OrderSimpleQueryDto(Long orderId, String name, LocalDateTime orderDate, orderStatus orderStatus, Address address) {
            this.orderId = orderId;
            this.name = name;
            this.orderDate = orderDate;
            this.orderStatus = orderStatus;
            this.address = address;
        }
         */
}