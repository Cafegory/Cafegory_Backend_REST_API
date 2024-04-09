package com.example.demo.dto.study;

import java.util.List;

import com.example.demo.dto.member.MemberResponse;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StudyMembersResponse {

	private final List<MemberResponse> joinedMembers;

}
