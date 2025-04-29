package com.springboot.tukserver.record.service;

import com.springboot.tukserver.crdnt.domain.Crdnt;
import com.springboot.tukserver.crdnt.dto.DrivingEndRequest;
import com.springboot.tukserver.crdnt.repository.CrdntRepository;
import com.springboot.tukserver.member.domain.Member;
import com.springboot.tukserver.member.repository.MemberRepository;
import com.springboot.tukserver.record.domain.DriveRecord;
import com.springboot.tukserver.record.dto.DriveRecordDTO;
import com.springboot.tukserver.record.dto.DrivingRouteResponse;
import com.springboot.tukserver.record.dto.OtherRouteResponse;
import com.springboot.tukserver.record.dto.RoutePoint;
import com.springboot.tukserver.record.repository.RecordRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final RecordRepository recordRepository;
    private final MemberRepository memberRepository;
    private final CrdntRepository crdntRepository;

    public List<DriveRecord> getRecordsByUserId(String userId) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        return recordRepository.findAllByMemberOrderByEndTimeDesc(member);
    }


    public List<DriveRecordDTO> getDriveRecordsByMember(Member member) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd HH:mm:ss");

        return recordRepository.findAllByMemberOrderByEndTimeDesc(member).stream()
                .map(record -> DriveRecordDTO.builder()
                        .recordId(record.getRecordId())
                        .startTime(record.getStartTime().format(formatter))  // 💡 포맷 적용
                        .build())
                .toList();
    }

    @Transactional
    public void saveDriveRecordWithRoute(DrivingEndRequest request) {
        // 현재 로그인된 사용자 정보 가져오기
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();

        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime start = LocalDateTime.parse(request.getStartTime(), formatter);
        LocalDateTime end = LocalDateTime.parse(request.getEndTime(), formatter);

        DriveRecord record = DriveRecord.builder()
                .startTime(start)
                .endTime(end)
                .member(member)
                .build();
        recordRepository.save(record);

        // 좌표 저장
        List<Crdnt> coordinates = request.getRoute().stream()
                .map(dto -> Crdnt.builder()
                        .latitude(dto.getLatitude())
                        .longitude(dto.getLongitude())
                        .accidentStatus(dto.getWarning())
                        .driveRecord(record)
                        .build())
                .toList();

        crdntRepository.saveAll(coordinates);
    }

    public List<Crdnt> getMyCoordinatesByRecordId(String userId, Long recordId) throws IllegalAccessException {
        DriveRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("주행기록을 찾을 수 없습니다."));

        if (!record.getMember().getUserId().equals(userId)) {
            throw new IllegalAccessException("본인의 주행 기록만 조회할 수 있습니다.");
        }

        return crdntRepository.findByDriveRecord_RecordId(recordId);
    }

    public DrivingRouteResponse getRouteByRecordId(Long recordId, String userId) {
        DriveRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("주행기록이 없습니다."));

        if (!record.getMember().getUserId().equals(userId)) {
            throw new RuntimeException("본인의 기록이 아닙니다.");
        }

        List<Crdnt> coordinates = crdntRepository.findByDriveRecord(record);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd HH:mm:ss");

        List<RoutePoint> route = coordinates.stream().map(c -> new RoutePoint(
                c.getLatitude(),
                c.getLongitude(),
                c.getAccidentStatus()
        )).toList();

        return DrivingRouteResponse.builder()
                .startTime(record.getStartTime().format(formatter))
                .endTime(record.getEndTime().format(formatter))
                .route(route)
                .build();
    }

    public OtherRouteResponse getOtherMemberRoute(Long recordId, String currentUserId) {
        Member currentMember = memberRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new RuntimeException("현재 멤버를 찾을 수 없습니다."));

        DriveRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("주행 기록을 찾을 수 없습니다."));

        // 🔒 같은 팀인지 확인
        if (!record.getMember().getTeam().equals(currentMember.getTeam())) {
            throw new AccessDeniedException("같은 팀 멤버만 조회할 수 있습니다.");
        }

        // ⛳ 좌표 리스트 가져오기
        List<Crdnt> crdnts = crdntRepository.findByDriveRecord(record);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd HH:mm:ss");

        // 🧩 변환
        List<OtherRouteResponse.RoutePoint> routePoints = crdnts.stream()
                .map(c -> OtherRouteResponse.RoutePoint.builder()
                        .latitude(c.getLatitude())
                        .longitude(c.getLongitude())
                        .warning(c.getAccidentStatus())
                        .build()
                ).toList();

        return OtherRouteResponse.builder()
                .startTime(record.getStartTime().format(formatter))
                .endTime(record.getEndTime().format(formatter))
                .route(crdnts.stream().map(c -> OtherRouteResponse.RoutePoint.builder()
                        .latitude(c.getLatitude())
                        .longitude(c.getLongitude())
                        .warning(c.getAccidentStatus())
                        .build()).toList())
                .build();
    }


}
