package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") // 매핑할 이름
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue //PK
    @Column(name = "order_id")
    private Long id;

    @ManyToOne //Order의 입장에서 Member = 다 대 1
    @JoinColumn(name = "member_id")
    private Member member;
    /*
    양방향 참조이다.(Member에도 Order가 있음)
    FK는 Order가 갖고있다.
    -> DB에서는 주인이라는 개념을 넣고, 둘 중에 하나만 변경 시 참조하도록 JPA가 설정함.
    -> Member는 orders와 Order를 변경해야함 || Order는 FK(Member_id)만 변경하면 됨
    -> Order가 연관관계 주인임.
*/

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne
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
}
