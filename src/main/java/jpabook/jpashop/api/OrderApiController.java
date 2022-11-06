package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.service.OrderSearch;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();

            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());
        }
        return all;
    }

    /*
    Order에서 Member는 N 대 1 && Lazy
    Order에서 Delivery는 1 대 1 && Lazy
    Order에서 OrderItem은 1 대 N && Lazy
    -> LAZY이기 때문에 프록시를 가져오기 때문에, 강제 초기화 (호출)을 해줌.
    -> OrderItem의 목록들은 N번 뻥튀기됨.
    -> 이 코드는 엔티티를 직접 노출 & 쿼리가 많이나가서 쓰레기인 코드이다.
    * */


    @GetMapping("/api/v2/orders")
    public List<OrderDto> orderV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> collect = orders.stream().map(o -> new OrderDto(o)).collect(Collectors.toList());
        return collect;
    }
/*
    @Getter
    static class OrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItem> orderItems;

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            order.getOrderItems().stream().forEach(o -> o.getItem().getName());
            orderItems = order.getOrderItems();

        }
    }
*/
    /*
    잘 작성한 코드 처럼 보인다 ( Entity를 DTO로 래핑하여 반환했기 떄문에 )
    하지만 DTO안에 래핑되지 않은 List<OrderItem> Entity가 있다....이렇게 되면
    OrderItem Entity의 스펙이 외부로 공개됨 ㄷㄷ
    -> Entity와 완전히 연관을 끊어야 한다.
    -> OrderItem도 DTO로 바꿔야함.
    * */

    @Getter
    static class OrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            orderItems = order.getOrderItems().stream().map(o -> new OrderItemDto(o)).collect(Collectors.toList());
        }
    }

    @Data
    static class OrderItemDto {
        private String itemName; // 상품명
        private int orderPrice; // 주문 가격
        private int count; // 주문 수량

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }
    /*
    해당 결과를 보면, 고객의 주소정보 같은 민감 정보를 감춘 (잘 Dto로 래핑한)
    정보만 전송하고 다룬다.
    이와 같이 Entity를 외부로 노출하지 않는것은 Dto속에서의 Entity도 노출하면 안됨!!!!

    But 여기서는 N+1 문제가 발생함!
    order 2, Member 2, Delivery 2, OrderItem 4 Item 2
    더럽게 많이나감 더 더 최적화 해야됨.
    * */


    @GetMapping("/api/v3/orders")
    public List<OrderDto> orderV3() {
        List<Order> orders = orderRepository.findAllWithItem();
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return result;
    }
    /*
    이렇게 fetch 조인을 사용하면 N + 1 의 문제를 해결 할 수 있다.
    1 : N의 관계에서 1에서 N을 조회(Order에서 OrderItem)할 때
    데이터가 뻥튀기 되어버린다
    해결 -> createQuery에 distinct를 쓰면됨.
        -> DB에서 distinct는 완벽하게 일치하는 값만 찾아 없애지만, JPA에서 distinct는 같은 엔티티 조회 결과 중복도 걸러줌.

    하지만 아직 엄청난 단점이있는데
    그것은 페이징 처리를 할 수 없다는 것이다...
    * */
}
