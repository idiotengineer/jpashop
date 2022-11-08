package jpabook.jpashop.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> result = findOrders();

        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });
        return result;
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
                        "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                                " from OrderItem oi" +
                                " join oi.item i" +
                                " where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }


    public List<OrderQueryDto> findOrders() {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderQueryDto" +
                        "(o.id, m.name, o.orderDate, o.status, d.address)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderQueryDto.class
        ).getResultList();
    }


    /*
    더럽게 긴 JPQL을 씀.
    하지만 컬렉션을 바로 넣을 순 없다. -> OrderQueryDto의 생성자 & qlString에서 컬렉션은 넣지 않음.

    1. findOrderQueryDtos() 메서드 실행
    2. findOrders() 메서드로 QueryString을 사용해 Order로 부터 인수 4개(orderId, name, orderDate, orderStatus, address)로 새로운 OrderQueryDto를 만듬 -> result 객체 컬렉션
    3. result 객체 마다 갖고 있는 List 필드인 orderItems에 대해서 직접 루프를 돌면서 findOrderItems(Long orderId) 호출
    4. findOrderItems는 OrderQueryDto 객체의 orderId 값을 이용해 OrderItemQueryDto로 바꿈.


    쿼리 조회 횟수
    1. findOrders에서 Order, Member Delivery
    2. userA의 orderItem과 item (ManyToOne이기 떄문에 join 사용)
    3. userB도 반복

    총 3번으로 N + 1이다...
    * */
    public List<OrderQueryDto> findAllByDto_optimization() {
        List<OrderQueryDto> result = findOrders();

        List<Long> orderIds = result.stream()
                .map(o -> o.getOrderId())
                .collect(Collectors.toList());

        List<OrderItemQueryDto> orderItems = em.createQuery(
                        "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                                " from OrderItem oi" +
                                " join oi.item i" +
                                " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();

        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
                .collect(Collectors.groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()));

        result.forEach(o->o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return result;
    }

    /*
        1. result =  findOrders로 Order를 Dto로 매핑함 (Order기준 ToMany인 orderItem 제외)
        2. orderIds = result의 id값인 리스트를 만듬.
        2. orderItems = Order의 XToMany인 OrderItem을 item과 조인하여 (orderIds와 일치한 id 값을) 필요한 데이터만 매핑하여 Dto로 만듬 -> OrderItemQueryDto
        3. orderItemMap = orderItems와 ID로 Map을 만듬
        5. result의 각각의 값의 OrderItem을 orderItemMap으로 설정
    * */


}
