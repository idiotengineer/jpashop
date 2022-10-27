package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable // JPA의 내장타입임을 선언
@Getter

public class Address {

    private String city;
    private String street;
    private String zipcode;


}
