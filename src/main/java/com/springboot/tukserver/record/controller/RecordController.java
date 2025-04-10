package com.springboot.tukserver.record.controller;

import com.springboot.tukserver.crdnt.domain.Crdnt;
import com.springboot.tukserver.crdnt.dto.DrivingEndRequest;
import com.springboot.tukserver.member.domain.Member;
import com.springboot.tukserver.member.repository.MemberRepository;
import com.springboot.tukserver.record.domain.DriveRecord;
import com.springboot.tukserver.ApiResponse;
import com.springboot.tukserver.record.dto.DriveRecordDTO;
import com.springboot.tukserver.record.dto.DrivingRouteResponse;
import com.springboot.tukserver.record.dto.OtherRouteResponse;
import com.springboot.tukserver.record.dto.RecordResponse;
import com.springboot.tukserver.record.repository.RecordRepository;
import com.springboot.tukserver.record.service.RecordService;
import com.springboot.tukserver.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/record")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;
    private final MemberRepository memberRepository;

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

    @GetMapping("/member/{memberId}")
    public ResponseEntity<ApiResponse<List<DriveRecordDTO>>> getRecordsOfTeamMember(@PathVariable Long memberId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication.getPrincipal() instanceof CustomUserDetails customUser)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "인증되지 않은 사용자입니다.", null));
        }

        Member requester = memberRepository.findByUserId(customUser.getUsername())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Member target = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("대상 멤버를 찾을 수 없습니다."));

        if (requester.getTeam() == null || target.getTeam() == null ||
                !requester.getTeam().getTeamId().equals(target.getTeam().getTeamId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, "같은 팀의 멤버만 조회할 수 있습니다.", null));
        }

        List<DriveRecordDTO> records = recordService.getDriveRecordsByMember(target);
        return ResponseEntity.ok(new ApiResponse<>(true, "주행기록 조회 성공", records));
    }

    @PostMapping("/end")
    public ResponseEntity<ApiResponse<Void>> saveDriveRecord(@RequestBody DrivingEndRequest request) {
        recordService.saveDriveRecordWithRoute(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "주행기록 저장 완료", null));
    }

    @GetMapping("/my/route/{recordId}")
    public ResponseEntity<ApiResponse<DrivingRouteResponse>> getMyRoute(
            @PathVariable Long recordId
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof CustomUserDetails customUser)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "인증되지 않은 사용자입니다.", null));
        }

        DrivingRouteResponse response = recordService.getRouteByRecordId(recordId, customUser.getUsername());

        return ResponseEntity.ok(new ApiResponse<>(true, "주행 기록 좌표 조회 성공", response));
    }

    @GetMapping("/other/route/{recordId}")
    public ResponseEntity<ApiResponse<OtherRouteResponse>> getOtherMemberRoute(@PathVariable Long recordId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUserId = auth.getName();

        OtherRouteResponse response = recordService.getOtherMemberRoute(recordId, currentUserId);

        return ResponseEntity.ok(new ApiResponse<>(true, "선택한 멤버의 주행 경로 조회 성공", response));
    }
}
