package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.service.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all){
            order.getMember().getName();
            order.getDelivery().getAddress();

            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o->o.getItem().getName());
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
}
