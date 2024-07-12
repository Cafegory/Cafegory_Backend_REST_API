package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.example.demo.helper.CafeSaveHelper;
import com.example.demo.helper.MemberSaveHelper;
import com.example.demo.helper.ReviewSaveHelper;
import com.example.demo.helper.StudyMemberSaveHelper;
import com.example.demo.helper.StudyOnceCommentSaveHelper;
import com.example.demo.helper.StudyOnceSaveHelper;
import com.example.demo.helper.ThumbnailImageSaveHelper;
import com.example.demo.repository.cafe.CafeRepository;
import com.example.demo.repository.member.MemberRepository;
import com.example.demo.repository.review.ReviewRepository;
import com.example.demo.repository.study.StudyMemberRepository;
import com.example.demo.repository.study.StudyOnceCommentRepository;
import com.example.demo.repository.study.StudyOnceRepository;
import com.example.demo.repository.thumbnailimage.ThumbnailImageRepository;

@TestConfiguration
public class TestConfig {

	@Autowired
	private CafeRepository cafeRepository;
	@Autowired
	private ReviewRepository reviewRepository;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private StudyMemberRepository studyMemberRepository;
	@Autowired
	private StudyOnceCommentRepository studyOnceCommentRepository;
	@Autowired
	private StudyOnceRepository studyOnceRepository;
	@Autowired
	private ThumbnailImageRepository thumbnailImageRepository;

	@Bean
	public CafeSaveHelper cafeSaveHelper() {
		return new CafeSaveHelper(cafeRepository);
	}

	@Bean
	public MemberSaveHelper memberSaveHelper() {
		return new MemberSaveHelper(memberRepository, thumbnailImageRepository);
	}

	@Bean
	public ReviewSaveHelper reviewSaveHelper() {
		return new ReviewSaveHelper(reviewRepository, cafeRepository, memberRepository);
	}

	@Bean
	public StudyMemberSaveHelper studyMemberSaveHelper() {
		return new StudyMemberSaveHelper(studyMemberRepository, memberRepository, studyOnceRepository);
	}

	@Bean
	public StudyOnceCommentSaveHelper studyOnceCommentSaveHelper() {
		return new StudyOnceCommentSaveHelper(studyOnceCommentRepository, memberRepository, studyOnceRepository);
	}

	@Bean
	public StudyOnceSaveHelper studyOnceSaveHelper() {
		return new StudyOnceSaveHelper(studyOnceRepository, memberRepository, cafeRepository);
	}

	@Bean
	public ThumbnailImageSaveHelper thumbnailImageSaveHelper() {
		return new ThumbnailImageSaveHelper(thumbnailImageRepository);
	}

}
