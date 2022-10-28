package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor // Lombok + 자동 Autowired
@Transactional(readOnly = true) // JPA가 조회하는 부분에서 성능 최적화를 함 (더티체킹 안함, 리소스 제한 등...... 나중에 개념을 확인)
public class MemberService {

    @Autowired
    MemberRepository memberRepository;

    //회원 가입
    @Transactional // 쓰기 쓰는 부분인 Join에는 Transactional의 readOnlxy = false가 default값이기 때문에 중첩해서 씀.
    public Long join(Member member) {
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    //회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }
}
