package com.example.demo.domain;

import java.time.LocalDateTime;

import com.example.demo.domain.cafe.CafeImpl;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.MemberImpl;

public interface StudyOnce {

	void tryJoin(Member memberThatExpectedToJoin, LocalDateTime requestTime);

	/**
	 * 스터디 참여를 취소하는 로직이 구현된 메서드
	 * @param memberThatExpectedToQuit 스터디 참여를 취소하고자 하는 멤버
	 * @param requestTime 취소 요청 시간
	 */
	StudyMember tryQuit(Member memberThatExpectedToQuit, LocalDateTime requestTime);

	void updateAttendance(Member leader, Member member, boolean attendance);

	void changeCafe(CafeImpl cafe);

	boolean isLeader(MemberImpl member);

}
