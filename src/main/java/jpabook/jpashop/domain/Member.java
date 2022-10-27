package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Member {

    @Id
    @GeneratedValue //PK
    @Column(name = "member_id") // 매핑할 이름
    private Long id;

    private String name;

    @Embedded //JPA의 내장 타입 사용 선언
    private Address address;

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

    // Member의 입장에서 Order = 1 대 다 -> OneToMany
    /*
    양방향 참조이다.(Member에도 Order가 있음)
    FK는 Order가 갖고있다.
    -> DB에서는 주인이라는 개념을 넣고, 둘 중에 하나만 변경 시 참조하도록 JPA가 설정함.
    -> Member는 Member.orders와 Order를 변경해야함 || Order는 FK(Member_id)만 변경하면 됨
    -> Order가 연관관계 주인임.
        -> mappedBy = member로 지정 -> order.member에 의해 매핑된 것 임을 알림.
        -> orders의 값을 넣어도 FK의 값은 변경되지 않음.
*/


}
