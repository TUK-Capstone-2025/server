package member.service;

import jdk.jfr.Registered;
import lombok.RequiredArgsConstructor;
import member.domain.Member;
import member.dto.MemberRegisterRequest;
import member.dto.MemberRegisterResponse;
import member.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberRegisterResponse register(MemberRegisterRequest request){
        duplicationUserId(request.userId());
        Member member = Member.builder()
                .userId(request.userId())
                .password(request.password())
                .name(request.name())
                .email(request.email())
                .phone(request.phone())
                .nickname(request.nickname())
                .build();
        memberRepository.save(member);
        return new MemberRegisterResponse(member.getId());
    }

    private void duplicationUserId(String userId){
        if(memberRepository.existsByLoginId(userId)){

        }
    }


}

