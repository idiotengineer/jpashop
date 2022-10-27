package jpabook.jpashop.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
@DiscriminatorValue("A") // 싱글테이블에서 저장할 때 구분하기 위함 (A = Album임)

public class Album extends Item {
    private String artist;
    private String etc;
}
