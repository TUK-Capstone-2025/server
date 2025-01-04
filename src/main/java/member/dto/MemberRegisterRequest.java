package member.dto;

public record MemberRegisterRequest(
        String userId,
        String password,
        String email,
        String phone,
        String name,
        String nickname
) {
}
