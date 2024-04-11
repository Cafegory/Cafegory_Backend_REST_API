package com.example.demo.service.study;

import javax.persistence.EntityManager;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.config.TestConfig;
import com.example.demo.domain.cafe.Cafe;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.ThumbnailImage;
import com.example.demo.domain.study.StudyOnce;
import com.example.demo.domain.study.StudyOnceComment;
import com.example.demo.dto.study.StudyOnceCommentResponse;
import com.example.demo.helper.CafePersistHelper;
import com.example.demo.helper.MemberPersistHelper;
import com.example.demo.helper.StudyOnceCommentPersistHelper;
import com.example.demo.helper.StudyOncePersistHelper;
import com.example.demo.helper.ThumbnailImagePersistHelper;

@SpringBootTest
@Import(TestConfig.class)
@Transactional
class StudyOnceQAndAQueryServiceImplTest {
	@Autowired
	private StudyOnceQAndAQueryService studyOnceQAndAQueryService;
	@Autowired
	private MemberPersistHelper memberPersistHelper;
	@Autowired
	private ThumbnailImagePersistHelper thumbnailImagePersistHelper;
	@Autowired
	private StudyOncePersistHelper studyOncePersistHelper;
	@Autowired
	private CafePersistHelper cafePersistHelper;
	@Autowired
	private StudyOnceCommentPersistHelper studyOnceCommentPersistHelper;
	@Autowired
	private EntityManager em;

	@Test
	@DisplayName("카공 질문을 찾는다")
	void searchQuestion() {
		//given
		ThumbnailImage thumb = thumbnailImagePersistHelper.persistDefaultThumbnailImage();
		Member leader = memberPersistHelper.persistMemberWithName(thumb, "카공장");
		Member otherPerson = memberPersistHelper.persistMemberWithName(thumb, "김동현");
		Cafe cafe = cafePersistHelper.persistDefaultCafe();
		StudyOnce studyOnce = studyOncePersistHelper.persistDefaultStudyOnce(cafe, leader);
		StudyOnceComment studyOnceComment = studyOnceCommentPersistHelper.persistDefaultStudyOnceQuestion(
			otherPerson, studyOnce);
		em.flush();
		em.clear();
		//when
		StudyOnceCommentResponse response = studyOnceQAndAQueryService.searchComment(studyOnceComment.getId());
		Assertions.assertThat(response).isNotNull();
	}

}
