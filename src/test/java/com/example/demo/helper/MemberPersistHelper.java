package com.example.demo.helper;

import com.example.demo.builder.TestMemberBuilder;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.ThumbnailImage;
import com.example.demo.helper.entitymanager.EntityManagerForPersistHelper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberPersistHelper {

	private final EntityManagerForPersistHelper<Member> em;

	public Member persistDefaultMember(ThumbnailImage thumbnailImage) {
		Member member = new TestMemberBuilder().thumbnailImage(thumbnailImage).build();
		return em.save(member);
	}

	public Member persistMemberWithName(ThumbnailImage thumbnailImage, String name) {
		Member member = new TestMemberBuilder().name(name).thumbnailImage(thumbnailImage).build();
		return em.save(member);
	}
}
