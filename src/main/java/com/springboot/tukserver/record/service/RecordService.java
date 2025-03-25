package com.springboot.tukserver.record.service;

import com.springboot.tukserver.member.domain.Member;
import com.springboot.tukserver.member.repository.MemberRepository;
import com.springboot.tukserver.record.domain.DriveRecord;
import com.springboot.tukserver.record.repository.RecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final RecordRepository recordRepository;
    private final MemberRepository memberRepository;

    public List<DriveRecord> getRecordsByUserId(String userId) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        return recordRepository.findAllByMemberOrderByStartTimeDesc(member);
    }
}
