package com.springboot.tukserver.record.controller;

import com.springboot.tukserver.record.domain.DriveRecord;
import com.springboot.tukserver.ApiResponse;
import com.springboot.tukserver.record.dto.RecordResponse;
import com.springboot.tukserver.record.service.RecordService;
import com.springboot.tukserver.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/record")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<RecordResponse>>> getDriveRecordList() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            if (!(principal instanceof CustomUserDetails customUser)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "로그인이 필요합니다.", null));
            }

            String userId = customUser.getUsername();
            List<DriveRecord> recordList = recordService.getRecordsByUserId(userId);

            if (recordList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "주행기록이 없습니다.", null));
            }

            List<RecordResponse> responseList = recordList.stream()
                    .map(r -> new RecordResponse(r.getRecordId(), r.getStartTime()))
                    .toList();

            return ResponseEntity.ok(new ApiResponse<>(true, "주행기록 목록 반환", responseList));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "서버 오류: " + e.getMessage(), null));
        }
    }

}
