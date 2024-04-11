package com.example.demo.helper;

import com.example.demo.builder.TestMemberBuilder;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.ThumbnailImage;
import com.example.demo.repository.member.MemberRepository;

public class MemberPersistHelperImpl extends MemberPersistHelper {
	private final MemberRepository memberRepository;

	public MemberPersistHelperImpl(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	@Override
	public Member persistDefaultMember(ThumbnailImage thumbnailImage) {
		Member member = new TestMemberBuilder().thumbnailImage(thumbnailImage).build();
		return memberRepository.save(member);
	}

	@Override
	public Member persistMemberWithName(ThumbnailImage thumbnailImage, String name) {
		Member member = new TestMemberBuilder().name(name).thumbnailImage(thumbnailImage).build();
		return memberRepository.save(member);
	}
}
