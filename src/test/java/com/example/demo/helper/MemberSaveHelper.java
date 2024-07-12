package com.example.demo.helper;

import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.ThumbnailImage;
import com.example.demo.factory.TestMemberFactory;
import com.example.demo.repository.member.MemberRepository;
import com.example.demo.repository.thumbnailimage.ThumbnailImageRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
public class MemberSaveHelper {

	private final MemberRepository memberRepository;
	private final ThumbnailImageRepository thumbnailImageRepository;

	public Member saveMember(ThumbnailImage thumbnailImage) {
		ThumbnailImage mergedThumbnailImage = thumbnailImageRepository.save(thumbnailImage);
		Member member = TestMemberFactory.createMemberWithThumbnailImage(mergedThumbnailImage);
		return memberRepository.save(member);
	}

	public Member saveMemberWithName(ThumbnailImage thumbnailImage, String name) {
		ThumbnailImage mergedThumbnailImage = thumbnailImageRepository.save(thumbnailImage);
		Member member = TestMemberFactory.createMemberWithThumbAndName(mergedThumbnailImage, name);
		return memberRepository.save(member);
	}
}
