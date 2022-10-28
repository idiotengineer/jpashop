package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") // 매핑할 이름
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue //PK
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) //Order의 입장에서 Member = 다 대 1
    @JoinColumn(name = "member_id")
    private Member member;
    /*
    양방향 참조이다.(Member에도 Order가 있음)
    FK는 Order가 갖고있다.
    -> DB에서는 주인이라는 개념을 넣고, 둘 중에 하나만 변경 시 참조하도록 JPA가 설정함.
    -> Member는 orders와 Order를 변경해야함 || Order는 FK(Member_id)만 변경하면 됨
    -> Order가 연관관계 주인임.
*/

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    /*
    fetch = fetchType.LAZY ->
    즉시로딩 (FetchType.EAGER)은 예측이 어렵고, 어떤 SQL이 실행될지 예측이 어려움. 전부다 LAZY를 쓰는게 좋음.
    &&
    모든 엔티티는 목록을 각자 persist 해야함
    CascadeType.all->
    List의 목록을 하나씩 영속, 제거 해야하지만 CascadeType.All로 목록을 다 같이 진행해줌.
    * */

    @JoinColumn(name = "delivery_id")
    private Delivery delivery;
      /*
    1 대 1 상황에서 연관관계 주인 정하기
    -> 보통 Access를 많이 하는 쪽에 두눈게 편함
        -> Order에 Access를 해서 Delivery를 찾음
        -> Delivery를 찾아서 Order를 보진 않음
        -> Order에 FK를 두는 연관관계 주인을 잡기
            -> Order에 JoinColumn 설정 & Delivery에서 mapped 설정
    * */

    private LocalDateTime orderDate;

    private OrderStatus status;


    // == 연관관계 메서드 == //
    /*
        양방향 연결관계 세팅 시
        Ex) Order & Member
            Member가 주문 시 -> List <order> orders에 주문을 넣어야함.
            그래야 Member.getorder에서 주문 조회, Order.orderItems의 주문 목록 조회 가능
            그리고 Member에 저장 시, Order에도 같은 기능을 수행해놓아야 함. 그것에 대한 기능을 하는것이
            연관관계 메서드임. (누락도 방지해줌)
    * */
    public void setMember(Member member){
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }


    // == 생성 메서드 == //
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);

        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }


    // == 비즈니스 로직 == //
    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.COMP) { // 배송완료
            throw new IllegalStateException("이미 배송완료 된 상품은 취소가 불가능합니다");
        }

        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

    // == 조회 로직 == //
    // 전체 주문 가격 조회
    public int getTotalPrice() {
        int totalPrice = orderItems.stream().mapToInt(OrderItem::getTotalPrice).sum();

        /* 위와 같은 코드
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        */
        return totalPrice;
    }
}
