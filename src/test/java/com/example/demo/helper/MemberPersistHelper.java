package com.example.demo.helper;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.example.demo.builder.TestMemberBuilder;
import com.example.demo.domain.member.MemberImpl;
import com.example.demo.domain.member.ThumbnailImage;

public class MemberPersistHelper {

	@PersistenceContext
	private EntityManager em;

	public MemberImpl persistDefaultMember(ThumbnailImage thumbnailImage) {
		MemberImpl member = new TestMemberBuilder().thumbnailImage(thumbnailImage).build();
		em.persist(member);
		return member;
	}

	public MemberImpl persistMemberWithName(ThumbnailImage thumbnailImage, String name) {
		MemberImpl member = new TestMemberBuilder().name(name).thumbnailImage(thumbnailImage).build();
		em.persist(member);
		return member;
	}
}
