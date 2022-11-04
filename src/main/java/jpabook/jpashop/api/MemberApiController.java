package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;


    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) { // @Valid로 제약조건 확인
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }
    /*
        1. 화면(컨트롤러)에서 오는 요청에 대한 검증로직이 Entity에 들어가 있음. (어떤 곳에서는 @NotEmpty가 필요 없을 수 있다)
        2. 만약 name을 username으로 필드명을 변경해버리면 api가 깨질 수 있다...
            -> Entity를 건드려서 api의 스펙이 바뀌어버림... (api는 여러곳에서 방대하게 쓰임)
            -> Entity는 외부에서 바인딩받아서 쓰면 안되고, Dto (Data Transfer Object)를 쓰는게 맞음. 그래야 장애가 안남. && Entity를 노출 하지말자!!
    * */


    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.name);

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }
    /*
        기존 코드보다 조금 복잡해짐
        1. 파라미터에 Entity가 들어가지 않음 ( 데이터 전송 객체 )

        장점
        1. 필드명에 대한 api가 깨지지 않는다(api 스펙이 변경되지 않음)
        2. Member를 쓴다면? Member의 어떤 필드에 파라미터 값이 들어오는지 모름.
            -> DTO를 사용함으로 API스펙 자체에 맞춘 객체를 Member로 사용가능!!
        3. Entity에 NotEmpty를 넣는게 아닌, DTO에 NotEmpty를 Valid로 사용가능
            -> Valid 값이 다른 환경(어떤곳은 NotEmpty가 필요하고, 어떤곳은 NotEmpty가 있으면 안되는 API)에서 별도의 DTO로 커스터마이징해서 사용가능

    * */


    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request){

        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    } // 파라미터와 리턴이 다름
    //이정도는 설명 없어도 될듯.

    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        return memberService.findMembers();
    }
    /*
    문제점
    1. Api로 해당 Entity(Member)를 전송을 할 때 모든 정보가 노출됨 && private List<Order> orders = new ArrayList<>(); 또한 노출됨.
        -> Member에 @JsonIgnore를 쓰면 되지만,,,, api가 또 깨짐 -> DTO를 써야함.
    2. Entity의 필드를 바꾸면,,, API 스펙도 다 깨짐

    출력 -> 유연성이 안좋은 배열이 그냥 출력됨.
    [
    {
        "id": 1,
        "name": "new_hello",
        "address": null,
        "orders": []
    },
    {
        "id": 2,
        "name": "member1",
        "address": {
            "city": "서울",
            "street": "test",
            "zipcode": "1111"
        },
        "orders": []
    },
    {
        "id": 3,
        "name": "member2",
        "address": {
            "city": "부산",
            "street": "qweq",
            "zipcode": "123123"
        },
        "orders": []
    }
]

    * */

    @GetMapping("/api/v2/members")
    public Result memberV2() {
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());

        return new Result(collect);
    }
    /*
    memberV1의 리턴값(List<Member>)를 Result<Generic>으로 바꾸어서 래핑함.
    출력 -> 유연성이 좋게 data로 배열을 감싸서 출력함.
    "data": [
        {
            "name": "new_hello"
        },
        {
            "name": "member1"
        },
        {
            "name": "member2"
        }
    ]

    * */

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }


    @Data
    static class CreateMemberRequest {
        private String name;
    }
    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

}
