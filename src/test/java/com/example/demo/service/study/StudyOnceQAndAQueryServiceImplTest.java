package com.example.demo.service.study;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.demo.domain.cafe.Cafe;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.study.StudyOnce;
import com.example.demo.domain.study.StudyOnceComment;
import com.example.demo.dto.study.StudyOnceCommentResponse;
import com.example.demo.repository.study.InMemoryStudyOnceCommentRepository;
import com.example.demo.service.ServiceTest;

class StudyOnceQAndAQueryServiceImplTest extends ServiceTest {
	private final InMemoryStudyOnceCommentRepository studyOnceCommentRepository =
		InMemoryStudyOnceCommentRepository.INSTANCE;
	private final StudyOnceQAndAQueryService studyOnceQAndAQueryService = new StudyOnceQAndAQueryServiceImpl(
		studyOnceCommentRepository, memberMapper, studyOnceMapper);

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
