package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }

    @Transactional
    public void updateItem(Long itemId, String name, int price,int stockQuantity) {
        Item findItem = itemRepository.findOne(itemId);
        findItem.setPrice(price);
        findItem.setName(name);
        findItem.setStockQuantity(stockQuantity);

        /*
        1. 변경감지로 하는 업데이트 방법
        Item은 영속상태이므로 ItemRepository의 save 메서드를 호출 할 필요 없이
        스프링이 @Transactional로 commit을 함. -> JPA가 flush를 호출 -> 더티체킹 실시 -> 더티체킹 된 값을 업데이트 쿼리로 날려줌

        2. 병합
        준영속 상태 엔티티를 영속상태로 변경

            (1) [Controller]
            itemService.saveItem 실행

            (2) [ItemService]
            itemRepository.save(item) 실행

            (3) [ItemRepository]
            if.. else 중 em.merge 호출 ( 우린 item의 id값이 있음 )


            merge?
            먼저 1차 캐시에서 엔티티를 찾아 item으로 덮는다. 1차 캐시에 매칭된 데이터가 없으면? DB에서 엔티티를 찾아 item으로 덮어씌움.
            리턴은 덮어씌워진 엔티티를 반환함. -> 모든 데이터를 덮어씌움. 선택적으로 덮어씌우기 불가능(없으면 null로)
            -> 위험해서 merge는 잘 안씀
        * */
    }
}
