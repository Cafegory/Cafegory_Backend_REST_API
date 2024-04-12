package com.example.demo.service.study;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.demo.domain.cafe.Cafe;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.ThumbnailImage;
import com.example.demo.domain.study.StudyOnce;
import com.example.demo.domain.study.StudyOnceComment;
import com.example.demo.dto.study.StudyOnceCommentResponse;
import com.example.demo.helper.CafePersistHelper;
import com.example.demo.helper.CafePersistHelperImpl;
import com.example.demo.helper.MemberPersistHelper;
import com.example.demo.helper.MemberPersistHelperImpl;
import com.example.demo.helper.StudyOnceCommentPersistHelper;
import com.example.demo.helper.StudyOnceCommentPersistHelperImpl;
import com.example.demo.helper.StudyOncePersistHelper;
import com.example.demo.helper.StudyOncePersistHelperImpl;
import com.example.demo.mapper.MemberMapper;
import com.example.demo.mapper.StudyOnceMapper;
import com.example.demo.repository.cafe.InMemoryCafeRepository;
import com.example.demo.repository.member.InMemoryMemberRepository;
import com.example.demo.repository.study.InMemoryStudyOnceCommentRepository;
import com.example.demo.repository.study.InMemoryStudyOnceRepository;

class StudyOnceQAndAQueryServiceImplTest {
	public static final ThumbnailImage THUMBNAIL_IMAGE = new ThumbnailImage(1L, "a");
	private final InMemoryStudyOnceCommentRepository studyOnceCommentRepository = new InMemoryStudyOnceCommentRepository();
	private final StudyOnceQAndAQueryService studyOnceQAndAQueryService = new StudyOnceQAndAQueryServiceImpl(
		studyOnceCommentRepository, new MemberMapper(), new StudyOnceMapper());
	private final InMemoryMemberRepository memberRepository = new InMemoryMemberRepository();
	private final MemberPersistHelper memberPersistHelper = new MemberPersistHelperImpl(memberRepository);
	private final InMemoryStudyOnceRepository studyOnceRepository = new InMemoryStudyOnceRepository();
	private final StudyOncePersistHelper studyOncePersistHelper = new StudyOncePersistHelperImpl(studyOnceRepository);
	private final InMemoryCafeRepository cafeRepository = new InMemoryCafeRepository();
	private final CafePersistHelper cafePersistHelper = new CafePersistHelperImpl(cafeRepository);
	private final StudyOnceCommentPersistHelper studyOnceCommentPersistHelper = new StudyOnceCommentPersistHelperImpl(
		studyOnceCommentRepository);

	@Test
	@DisplayName("카공 질문을 찾는다")
	void searchQuestion() {
		Member leader = memberPersistHelper.persistMemberWithName(THUMBNAIL_IMAGE, "카공장");
		Member otherPerson = memberPersistHelper.persistMemberWithName(THUMBNAIL_IMAGE, "김동현");
		Cafe cafe = cafePersistHelper.persistDefaultCafe();
		StudyOnce studyOnce = studyOncePersistHelper.persistDefaultStudyOnce(cafe, leader);
		StudyOnceComment studyOnceComment = studyOnceCommentPersistHelper.persistDefaultStudyOnceQuestion(
			otherPerson, studyOnce);

		StudyOnceCommentResponse response = studyOnceQAndAQueryService.searchComment(studyOnceComment.getId());

		Assertions.assertThat(response).isNotNull();
	}

}
