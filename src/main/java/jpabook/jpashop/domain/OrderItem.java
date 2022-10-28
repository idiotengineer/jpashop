package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class OrderItem {

    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

<<<<<<< HEAD
<<<<<<< HEAD
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne
=======
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
>>>>>>> topic
=======
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
>>>>>>> topic3
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice;

    private int count;
}