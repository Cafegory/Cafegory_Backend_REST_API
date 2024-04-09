package com.example.demo.mapper;

import com.example.demo.domain.member.MemberImpl;
import com.example.demo.dto.WriterResponse;
import com.example.demo.dto.member.MemberResponse;

public class MemberMapper {

	public WriterResponse toWriterResponse(MemberImpl member) {
		return new WriterResponse(member.getId(), member.getName(), member.getThumbnailImage().getThumbnailImage());
	}

	public MemberResponse toMemberResponse(MemberImpl member) {
		return new MemberResponse(member.getId(), member.getName(), member.getThumbnailImage().getThumbnailImage());
	}
}
