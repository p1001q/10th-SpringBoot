package com.example.week07.domain.mission.dto;

import lombok.Builder;

import java.time.LocalDate;

public class MissionReqDTO {

    // 가게 미션 생성 요청 DTO
    @Builder
    public record CreateMission(
            LocalDate deadline,   // 미션 기한
            Integer point,        // 성공 시 받을 포인트
            String conditional    // 미션 조건 (예: 12,000원 이상 식사)
    ) {}
}
