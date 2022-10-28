package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable // JPA의 내장타입임을 선언
@Getter
<<<<<<< HEAD
<<<<<<< HEAD

public class Address {
=======
public class Address { // 값 타입은 변경불가능하게 해야함 -> setter가 없음
>>>>>>> topic
=======
public class Address { // 값 타입은 변경불가능하게 해야함 -> setter가 없음
>>>>>>> topic3

    private String city;
    private String street;
    private String zipcode;

<<<<<<< HEAD
<<<<<<< HEAD

=======
    protected Address() {
    }

=======
    protected Address() {
    }

>>>>>>> topic3
    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
<<<<<<< HEAD
>>>>>>> topic
=======
>>>>>>> topic3
}
