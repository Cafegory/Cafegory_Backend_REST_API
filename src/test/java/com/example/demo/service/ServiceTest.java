package com.example.demo.service;

import org.junit.jupiter.api.BeforeEach;

import com.example.demo.domain.member.ThumbnailImage;
import com.example.demo.helper.CafePersistHelper;
import com.example.demo.helper.MemberPersistHelper;
import com.example.demo.helper.StudyOnceCommentPersistHelper;
import com.example.demo.helper.StudyOncePersistHelper;
import com.example.demo.helper.entitymanager.InMemoryEntityManagerAdaptor;
import com.example.demo.mapper.MemberMapper;
import com.example.demo.mapper.StudyMemberMapper;
import com.example.demo.mapper.StudyOnceCommentMapper;
import com.example.demo.mapper.StudyOnceMapper;
import com.example.demo.repository.cafe.InMemoryCafeRepository;
import com.example.demo.repository.member.InMemoryMemberRepository;
import com.example.demo.repository.review.InMemoryReviewRepository;
import com.example.demo.repository.study.InMemoryStudyMemberRepository;
import com.example.demo.repository.study.InMemoryStudyOnceCommentRepository;
import com.example.demo.repository.study.InMemoryStudyOnceRepository;

public class ServiceTest {
	protected static final ThumbnailImage THUMBNAIL_IMAGE = new ThumbnailImage(1L, "testUrl");

	protected final MemberPersistHelper memberPersistHelper = new MemberPersistHelper(
		new InMemoryEntityManagerAdaptor<>(InMemoryMemberRepository.INSTANCE::save));
	protected final StudyOncePersistHelper studyOncePersistHelper = new StudyOncePersistHelper(
		new InMemoryEntityManagerAdaptor<>(InMemoryStudyOnceRepository.INSTANCE::save));
	protected final CafePersistHelper cafePersistHelper = new CafePersistHelper(new InMemoryEntityManagerAdaptor<>(
		InMemoryCafeRepository.INSTANCE::save));
	protected final StudyOnceCommentPersistHelper studyOnceCommentPersistHelper = new StudyOnceCommentPersistHelper(
		new InMemoryEntityManagerAdaptor<>(InMemoryStudyOnceCommentRepository.INSTANCE::save));
	protected final StudyOnceMapper studyOnceMapper = new StudyOnceMapper();
	protected final StudyMemberMapper studyMemberMapper = new StudyMemberMapper();
	protected final MemberMapper memberMapper = new MemberMapper();
	protected final StudyOnceCommentMapper studyOnceCommentMapper = new StudyOnceCommentMapper();

	@BeforeEach
	void clearRepository() {
		InMemoryCafeRepository.INSTANCE.deleteAll();
		InMemoryStudyOnceRepository.INSTANCE.deleteAll();
		InMemoryStudyOnceCommentRepository.INSTANCE.deleteAll();
		InMemoryStudyMemberRepository.INSTANCE.deleteAll();
		InMemoryMemberRepository.INSTANCE.deleteAll();
		InMemoryReviewRepository.INSTANCE.deleteAll();
	}
}
