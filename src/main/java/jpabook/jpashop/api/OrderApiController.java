package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import jpabook.jpashop.service.OrderSearch;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

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
    WHy...?
        -> 메모리에 모든 DB를 불러와서 메모리에서 페이징을 시도 ( 메모리 초과 빵 )
    * */


    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit
    ) {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);

        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());
        return result;
    }

    /*
    http://localhost:8080/api/v3.1/orders?offset=1&limit=100 호출

    1~100 (원래 0부터 시작)
    userA를 날리고 userB부터 시작해서 조회 (Delivery, Member, Order에는 페이징 처리가 된 모습을 보임) 그 후 에는 여전히 N + 1 문제 발생

    N + 1 해결 방법
    1. application.yml에 hibernate에 default_batch_fetch_size 설정
    실행 ->
        OrderItem = where orderItem order_id in (4,11)이라고 찍혀 있음.
        -> 한번에 in 쿼리로 order_id 4 = userA, order_id 11 = userB를 가져옴.
            -> 대충 해석을 하자면, orders(맨처음) 리스트인 값을 보고 파악하여 관련된 연관된 in 쿼리로 batch_fetch_size 만큼 씩 가져옴.
            1 : N : N 의 쿼리를 1 : 1 : 1

        Item
        -> 마찬가지로 2,3,9,11의 상품 id를 한번에 in 쿼리로 order_id의 batch_fetch_size 만큼 씩 가져옴.


    2. @BatchSize 애노테이션
    * */


    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }

    /*
        특정화면에 fit한 Query를 쓸 때(대부분 화면처리)와 단순 API를 제어하는 패키지를 나눠 쓰기도 함.

        맨 위에 OrderDto를 쓰지 않은 이유
        1. OrderQueryRepository가 OrderApiController를 참조 (Repository가 Contorller를)해버리는 상황이기 떄문에.
        2. fit한 Dto로 만들어 내기 위해

        순서
            1. 일단 findOrderDto에서 findOrders를 호출하여 ToOne인 관계를 다 join해서 한번에 불러온다.
            2. findOrderItems를 호출해서 ToMany관계인 orderItems와 item을 채운다!
                -> 직접 쿼리를 작성해서
    * */

    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5() {
        return orderQueryRepository.findAllByDto_optimization();
    }

    /*
    총 쿼리는 2번나감
    * */
}
