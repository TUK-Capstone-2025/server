package com.springboot.tukserver.member.service;

import com.springboot.tukserver.member.domain.Member;
import com.springboot.tukserver.member.dto.MemberProfileResponse;
import com.springboot.tukserver.member.dto.MemberSimpleDTO;
import com.springboot.tukserver.member.domain.MemberStatus;
import com.springboot.tukserver.member.repository.MemberRepository;
import com.springboot.tukserver.team.domain.Team;
import com.springboot.tukserver.team.dto.TeamApplicationResponse;
import com.springboot.tukserver.team.dto.TeamResponse;
import com.springboot.tukserver.team.repository.TeamRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;


@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final PasswordEncoder passwordEncoder;

    public Member registerMember(String userId, String password, String name, String email, String nickname) {

        Member member = new Member();
        member.setUserId(userId);
        member.setPassword(passwordEncoder.encode(password));
        member.setName(name);
        member.setEmail(email);
        member.setNickname(nickname);
        this.memberRepository.save(member);
        return member;

    }

    public MemberService(MemberRepository memberRepository, TeamRepository teamRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.teamRepository = teamRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId).orElse(null);  // ✅ 존재하지 않으면 `null` 반환
    }


    @Transactional
    public void assignToTeam(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));

        if (member.getStatus().equals("APPROVE")) {
            throw new IllegalStateException("이미 승인된 멤버입니다.");
        }

        Team team = member.getTeam();
        member.setStatus(MemberStatus.APPROVE);
        team.setMemberCount(team.getMemberCount() + 1);
        team.getMembers().add(member);

        memberRepository.save(member);
        teamRepository.save(team);
    }

    public void changePassword(String currentPassword, String newPassword) {
        // ✅ 현재 로그인된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName(); // 현재 로그인한 사용자의 아이디 가져오기

        // ✅ DB에서 사용자 정보 조회
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // ✅ 현재 비밀번호 검증
        if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 올바르지 않습니다.");
        }

        // ✅ 새 비밀번호 암호화 후 저장
        member.setPassword(passwordEncoder.encode(newPassword));
        memberRepository.save(member);
    }

    public void changeNickname(String newNickname) {
        // ✅ 현재 로그인된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName(); // 현재 로그인한 사용자의 아이디 가져오기

        // ✅ DB에서 사용자 정보 조회
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // ✅ 새 닉네임 설정 후 저장
        member.setNickname(newNickname);
        memberRepository.save(member);
    }

    @Transactional
    public void changeUserId(String newUserId) {
        // ✅ 현재 로그인된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserId = authentication.getName(); // 현재 로그인한 사용자의 아이디 가져오기

        // ✅ DB에서 사용자 정보 조회
        Member member = memberRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // ✅ 새 아이디가 이미 존재하는지 확인
        if (memberRepository.existsByUserId(newUserId)) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        // ✅ 새 아이디 설정 후 저장
        member.setUserId(newUserId);
        memberRepository.save(member);
    }

    @Transactional
    public void applyToTeam(Long memberId, Long teamId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("팀을 찾을 수 없습니다."));

        if (member.getTeam() != null) {
            throw new IllegalStateException("이미 팀에 신청했거나 배정된 상태입니다.");
        }

        // 💡 팀 정보만 넣고 상태는 REJECT
        member.setTeam(team);
        member.setStatus(MemberStatus.PENDING);

        memberRepository.save(member);
    }

    public List<MemberSimpleDTO> findPendingMembersByLeader(String leaderUserId) {
        Team team = teamRepository.findByLeader(leaderUserId)
                .orElseThrow(() -> new RuntimeException("리더의 팀을 찾을 수 없습니다."));

        return memberRepository.findByTeamAndStatus(team, MemberStatus.PENDING).stream()
                .map(member -> MemberSimpleDTO.builder()
                        .memberId(member.getMemberId())
                        .userId(member.getUserId())
                        .name(member.getName())
                        .nickname(member.getNickname())
                        .build())
                .toList();
    }

    public List<TeamApplicationResponse> getTeamApplications(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("해당 멤버를 찾을 수 없습니다."));

        List<Member> applications = memberRepository.findAllByUserId(member.getUserId());

        return applications.stream()
                .filter(m -> m.getTeam() != null) // 팀에 신청한 경우만 필터
                .map(m -> new TeamApplicationResponse(
                        m.getTeam().getTeamId(),
                        m.getTeam().getName(),
                        m.getStatus()
                ))
                .toList();
    }
    @Transactional
    public void approveMember(Long memberId, String leaderUserId) {
        Team team = teamRepository.findByLeader(leaderUserId)
                .orElseThrow(() -> new RuntimeException("팀을 찾을 수 없습니다."));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));

        if (!team.equals(member.getTeam())) {
            throw new IllegalArgumentException("해당 멤버는 이 팀에 속해 있지 않습니다.");
        }

        member.setStatus(MemberStatus.APPROVE);
        team.setMemberCount(team.getMemberCount() + 1);

        memberRepository.save(member);
        teamRepository.save(team);
    }

    @Transactional
    public void rejectMember(Long memberId, String leaderUserId) {
        Team team = teamRepository.findByLeader(leaderUserId)
                .orElseThrow(() -> new RuntimeException("팀을 찾을 수 없습니다."));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));

        if (!team.equals(member.getTeam())) {
            throw new IllegalArgumentException("해당 멤버는 이 팀에 속해 있지 않습니다.");
        }

        member.setTeam(null);
        member.setStatus(MemberStatus.REJECT);

        memberRepository.save(member);
    }

    @Transactional
    public void cancelTeamApplication(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));

        if (member.getStatus() != MemberStatus.PENDING) {
            throw new IllegalStateException("현재 신청 중인 상태가 아닙니다. 취소할 수 없습니다.");
        }

        member.setTeam(null);
        member.setStatus(MemberStatus.NONE); // 또는 REJECT
        memberRepository.save(member);
    }


    @Transactional
    public void kickOutMember(Long targetMemberId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserId = authentication.getName(); // 현재 로그인된 리더 ID

        Member leader = memberRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new RuntimeException("로그인한 리더 정보를 찾을 수 없습니다."));

        Team team = teamRepository.findByLeader(leader.getUserId())
                .orElseThrow(() -> new RuntimeException("해당 리더의 팀이 없습니다."));

        Member targetMember = memberRepository.findById(targetMemberId)
                .orElseThrow(() -> new RuntimeException("대상 멤버를 찾을 수 없습니다."));

        // 같은 팀인지 확인
        if (!team.equals(targetMember.getTeam())) {
            throw new IllegalStateException("이 멤버는 리더의 팀에 속해있지 않습니다.");
        }

        // 퇴출 처리
        targetMember.setTeam(null);
        targetMember.setStatus(MemberStatus.NONE);

        team.setMemberCount(team.getMemberCount() - 1);

        memberRepository.save(targetMember);
        teamRepository.save(team);
    }

    @Transactional
    public String saveProfileImage(String userId, MultipartFile file) throws IOException {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));

        // 저장할 경로 설정 (예: static/profile)
        String uploadDir = "src/main/resources/static/profile/";
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path path = Paths.get(uploadDir + fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());

        // DB에 저장할 URL 경로
        String imageUrl = "/profile/" + fileName;
        member.setProfileImageUrl(imageUrl);
        memberRepository.save(member);

        return imageUrl;
    }

    @Transactional(readOnly = true)
    public MemberProfileResponse getMemberProfile(Long targetMemberId, String requesterUserId) {
        Member requester = memberRepository.findByUserId(requesterUserId)
                .orElseThrow(() -> new RuntimeException("요청자를 찾을 수 없습니다."));

        Member target = memberRepository.findById(targetMemberId)
                .orElseThrow(() -> new RuntimeException("대상 멤버를 찾을 수 없습니다."));

        if (requester.getTeam() == null || target.getTeam() == null ||
                !requester.getTeam().getTeamId().equals(target.getTeam().getTeamId())) {
            throw new IllegalArgumentException("같은 팀이 아닙니다.");
        }

        return MemberProfileResponse.builder()
                .nickname(target.getNickname())
                .profileImageUrl(target.getProfileImageUrl())
                .build();
    }



}
