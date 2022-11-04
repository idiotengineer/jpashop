package jpabook.jpashop.api;


import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.service.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    @GetMapping("api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for(Order order : all){
            order.getMember().getName(); // Lazy 강제 초기화
            order.getDelivery().getAddress(); // Lazy 강제 초기화
        }
        return all;
    }

    /*
    ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
     @GetMapping("api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        return all;
    }
    ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
    OrderSearch = String name, OrderStatus orderStatus; // 주문 상태 만을 가지는 클래스이다.
    OrderSearch로 값을 찾아오기
    &&
    1. list를 반환하기 -> 무한루프 발생...
        Why..?
        ->  List<Order> all 추적 -> Order.member 추적 -> Member.orders -> ... 무한반복 (양방향 연관관계 문제 발생)

        해결방법
        - 양방향 연관관계 에서는 한쪽을 @JsonIgnore 해야함.... (Member의 List<Order> orders, OrderItem의 order, ... 전부 다)



    2. 500, ByteBuddyInterceptor 발생
        why..?
        -> 가지고 올 객체를 보자. List<Order>의 member 필드, delivery 필드는 지연로딩 (Lazy)
        따라서 진짜 객체를 DB에서 가져오는게 아닌, 가짜 프록시를 가져옴. ㅇㅇ 이것이 ByteBuddyInterceptor 클래스이다

        해결방법 1
        - 지연로딩일 경우 출력을 null로 가져오는의 외부라이브러리를 선택해도됨.(Hibernate5Module)
        - 사실 지금 이 것은 굳이 할 필요가 없긴 하다.

        해결방법 2
        for(Order order : all){
            order.getMember().getName(); // Lazy 강제 초기화
            order.getDelivery().getAddres(); // Lazy 강제 초기화
        }
        -> order.getMember()까지는 지연로딩이지만 프록시의 값 getName을 호출해서 쿼리를 나가게 함
        ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
        여기서도 즉시로딩 대신 지연로딩을 쓰는이유는?
        1. em.find 메서드같은 경우에는 즉시로딩을 써도 성능최적화가 가능하지만,
        findAllByString 같은 경우에는 JPQL이 날아감 -> 그대로 SQL이 번역 -> Order를 다 가져오고, 또 필요한 것들을 전부로딩...

        2. 다른 API에서 쓸 때 또 N + 1 문제가 터짐.


    * */
}
