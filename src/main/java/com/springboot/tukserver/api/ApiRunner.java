package com.springboot.tukserver.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

@Component
public class ApiRunner implements CommandLineRunner {

    @Autowired
    private ApiClient apiClient;

    @Override
    public void run(String... args) throws Exception {

        // ✅ 실행 여부를 환경변수 또는 설정으로 제어 (실제 서비스에서는 자동 실행 X)
        boolean shouldRunTests = Boolean.parseBoolean(System.getProperty("runApiTests", "false"));
        if (!shouldRunTests) {
            System.out.println("🔹 API 테스트 실행이 비활성화되어 있습니다.");
            return;  // 🔥 자동 실행 방지
        }

        // ✅ 1️⃣ 로그인 요청 (자동으로 JSESSIONID 저장됨)
        System.out.println("🔹 로그인 요청 중...");
        String loginResponse = apiClient.login("testuser", "testpassword");
        System.out.println("📌 로그인 결과: " + loginResponse);

        // ✅ 2️⃣ 로그인 후 사용자 정보 조회 (JSESSIONID 자동 추가)
        System.out.println("🔹 현재 로그인한 사용자 조회 중...");
        String userResponse = apiClient.getCurrentUser();
        System.out.println("📌 현재 사용자 정보: " + userResponse);

        // ✅ 3️⃣ 특정 사용자 팀 배정 (JSESSIONID 자동 추가)
        System.out.println("🔹 팀 배정 중...");
        String assignTeamResponse = apiClient.assignTeam(2L, 1L);
        System.out.println("📌 팀 배정 결과: " + assignTeamResponse);
    }


}
