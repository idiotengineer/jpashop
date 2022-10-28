package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // 싱글테이블 전략 (하나의 테이블에 Book, Album, Movie를 다 떄려넣음)
@DiscriminatorColumn(name = "dtype")
@Getter @Setter
public abstract class Item {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;

    private int price;

    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();


    //== 비즈니스 로직 ==//
    /*
     -> 보통 개발 시, Repository -> Service -> 결과 반환 -> Service -> Repository 로 가게 끔 개발을 하지만
        객체지향 적으로, 데이터를 가지고 있는 쪽에 비즈니스 로직이 있으면 사실 좋다. -> 관리하기 매우 편함.
    */
    // 재고 수량 증가
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    // 재고 수량 감소
    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;

        if (restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }

        this.stockQuantity = restStock;
    }
}
