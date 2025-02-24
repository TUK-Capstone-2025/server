package com.springboot.tukserver.api;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ApiClient {


    private final RestTemplate restTemplate;
    private final String BASE_URL = "http://localhost:8080/api"; // API 기본 URL
    private String sessionId;

    public ApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /** ✅ 회원가입 */
    public String register(String userId, String password, String name, String email, String nickname) {
        String url = BASE_URL + "/member/register";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = String.format("{ \"userId\": \"%s\", \"password\": \"%s\", \"name\": \"%s\", \"email\": \"%s\", \"nickname\": \"%s\" }",
                userId, password, name, email, nickname);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        return response.getBody();
    }

    /** ✅ 로그인 */
    public String login(String userId, String password) {
        String url = BASE_URL + "/member/login";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = String.format("{ \"userId\": \"%s\", \"password\": \"%s\" }", userId, password);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        List<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
        if (cookies != null) {
            for (String cookie : cookies) {
                if (cookie.startsWith("JSESSIONID")) {
                    sessionId = cookie.split(";")[0];  // "JSESSIONID=XXXX" 형태로 저장
                    System.out.println("📌 로그인 후 세션 ID 저장: " + sessionId);
                }
            }
        }

        return response.getBody();
    }

    /** ✅ 현재 로그인한 사용자 정보 조회 */
    public String getCurrentUser() {
        String url = BASE_URL + "/member/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (sessionId != null) {
            headers.set("Cookie", sessionId);  // ✅ 자동으로 JSESSIONID 추가
        }

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

        return response.getBody();
    }

    /** ✅ 특정 사용자를 팀에 배정 */
    public String assignTeam(Long memberId, Long teamId) {
        String url = BASE_URL + "/member/" + memberId + "/assignTeam/" + teamId;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        return response.getBody();
    }
}

