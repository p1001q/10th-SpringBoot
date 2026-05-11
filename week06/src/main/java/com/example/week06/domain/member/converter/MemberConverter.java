package com.example.week06.domain.member.converter;

import com.example.week06.domain.member.dto.MemberResDTO;
import com.example.week06.domain.member.entity.Member;

public class MemberConverter {

    // Member 엔티티를 마이페이지 응답 DTO로 변환
    public static MemberResDTO.MyPageInfo toMyPageInfo(Member member) {
        return MemberResDTO.MyPageInfo.builder()
                .name(member.getName())               // 닉네임
                .profileUrl(member.getProfileUrl())   // 프로필 이미지 URL
                .email(member.getEmail())             // 이메일
                .phoneNumber(member.getPhoneNumber()) // 휴대폰번호
                .point(member.getPoint())             // 포인트
                .build();
    }
}
