package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
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

    // @NotEmpty
    private String name;
        /* Controller의 Valid 값을 쓰기 위해 Entity에 Valid(제약조건) @NotEmpty를 삽입하면?
                1. 화면(컨트롤러)에서 오는 요청에 대한 검증로직이 Entity에 들어가 있음. (어떤 곳에서는 @NotEmpty가 필요 없을 수 있다)
                2. 만약 name을 username으로 필드명을 변경해버리면 api가 깨질 수 있다...
                    -> Entity를 건드려서 api의 스펙이 바뀌어버림... (api는 여러곳에서 방대하게 쓰임)
                    -> Entity는 외부에서 바인딩받아서 쓰면 안되고, Dto (Data Transfer Object)를 쓰는게 맞음. 그래야 장애가 안남. && Entity를 노출 하지말자!!
        * */

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
    -> * 주인의 값이 변경 시 FK의 값도 변경시킴
    -> Order가 연관관계 주인임.
        -> mappedBy = member로 지정 -> order.member에 의해 매핑된 것 임을 알림.
        -> orders의 값을 넣어도 FK의 값은 변경되지 않음.
*/


}
