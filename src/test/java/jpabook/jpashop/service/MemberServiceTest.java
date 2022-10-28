package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class) // 스프링과 함께 실행을 선언
@SpringBootTest // 스프링 실행 중에 테스트
@Transactional // Transactional을 쓰면 Rollback을 해버리기 때문에 insert 쿼리가 안나감.
class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em; // 쿼리 보는법 방법 2. 영속성 컨텍스트를 배출해내는 코드 작성

    @Test
    // @Rollback(value = false) //쿼리 보는법 방법 1.
    public void 회원가입() throws Exception {
        //given
        Member member = new Member();
        member.setName("kim");

        //when
        Long savedId = memberService.join(member);

        //then
        em.flush();
        Assert.assertEquals(member,memberRepository.findOne(savedId));
    }

    @Test
    public void 중복_회원_가입() throws Exception {

        //given
        Member member1 = new Member();
        member1.setName("kim1");

        Member member2 = new Member();
        member2.setName("kim1");

        //when
        memberService.join(member1);
        try {
            memberService.join(member2);
        } catch (IllegalStateException e) {
            return;
        }

        //then
        fail("에외가 발생해야 함");
    }
}