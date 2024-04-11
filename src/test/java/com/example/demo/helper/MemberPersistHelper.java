package com.example.demo.helper;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.example.demo.builder.TestMemberBuilder;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.ThumbnailImage;

public class MemberPersistHelper {

	@PersistenceContext
	private EntityManager em;

	public Member persistDefaultMember(ThumbnailImage thumbnailImage) {
		Member member = new TestMemberBuilder().thumbnailImage(thumbnailImage).build();
		em.persist(member);
		return member;
	}

	public Member persistMemberWithName(ThumbnailImage thumbnailImage, String name) {
		Member member = new TestMemberBuilder().name(name).thumbnailImage(thumbnailImage).build();
		em.persist(member);
		return member;
	}
}
