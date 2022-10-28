package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Delivery {

    @Id
    @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery",fetch = FetchType.LAZY)
    private Order order;
    /*
    1 대 1 상황에서 연관관계 주인 정하기
    -> 보통 Access를 많이 하는 쪽에 두눈게 편함
        -> Order에 Access를 해서 Delivery를 찾음
        -> Delivery를 찾아서 Order를 보진 않음
        -> Order에 FK를 두는 연관관계 주인을 잡기
            -> Order에 JoinColumn 설정 & Delivery에서 mapped 설정
    * */
    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING) // String 을 써야 장애가안남 ( 숫자라도 쓰자 )
    private DeliveryStatus status;
}
