package com.springboot.tukserver;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;  // ✅ 요청 성공 여부
    private String message;   // ✅ 응답 메시지
    private T data;

}
