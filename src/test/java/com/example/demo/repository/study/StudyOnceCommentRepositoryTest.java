package com.example.demo.repository.study;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.config.QueryDslConfig;
import com.example.demo.config.TestConfig;
import com.example.demo.domain.cafe.Cafe;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.ThumbnailImage;
import com.example.demo.domain.study.StudyOnce;
import com.example.demo.domain.study.StudyOnceComment;
import com.example.demo.helper.save.CafeSaveHelper;
import com.example.demo.helper.save.MemberSaveHelper;
import com.example.demo.helper.save.StudyOnceCommentSaveHelper;
import com.example.demo.helper.save.StudyOnceSaveHelper;
import com.example.demo.helper.save.ThumbnailImageSaveHelper;

@DataJpaTest
@Import({TestConfig.class, QueryDslConfig.class})
@Transactional
class StudyOnceCommentRepositoryTest {

	@Autowired
	private StudyOnceCommentRepository studyOnceCommentRepository;
	@Autowired
	private MemberSaveHelper memberPersistHelper;
	@Autowired
	private ThumbnailImageSaveHelper thumbnailImagePersistHelper;
	@Autowired
	private StudyOnceSaveHelper studyOncePersistHelper;
	@Autowired
	private CafeSaveHelper cafePersistHelper;
	@Autowired
	private StudyOnceCommentSaveHelper studyOnceCommentPersistHelper;
	@Autowired
	private EntityManager em;

	@Test
	@DisplayName("studyOnceCommentId로 오름차순 정렬된, studyOnceId로 댓글목록 조회")
	void findAllByStudyOnceId() {
		//given
		ThumbnailImage thumb = thumbnailImagePersistHelper.persistDefaultThumbnailImage();
		Member leader = memberPersistHelper.persistMemberWithName(thumb, "카공장");
		Member otherPerson = memberPersistHelper.persistMemberWithName(thumb, "김동현");
		Cafe cafe = cafePersistHelper.persistDefaultCafe();
		StudyOnce studyOnce = studyOncePersistHelper.persistDefaultStudyOnce(cafe, leader);
		StudyOnceComment question1 = studyOnceCommentPersistHelper.persistStudyOnceQuestionWithContent(
			otherPerson, studyOnce, "댓글1");
		StudyOnceComment question2 = studyOnceCommentPersistHelper.persistStudyOnceQuestionWithContent(
			otherPerson, studyOnce, "댓글2");
		studyOnceCommentPersistHelper.persistStudyOnceReplyWithContent(leader,
			studyOnce, question2, "대댓글2");
		studyOnceCommentPersistHelper.persistStudyOnceReplyWithContent(leader,
			studyOnce, question1, "대댓글1");

		em.flush();
		em.clear();
		//when
		List<StudyOnceComment> comments = studyOnceCommentRepository.findAllByStudyOnceId(studyOnce.getId());
		//then
		assertThat(comments.get(0).getContent()).isEqualTo("댓글1");
		assertThat(comments.get(1).getContent()).isEqualTo("댓글2");
		assertThat(comments.get(2).getContent()).isEqualTo("대댓글2");
		assertThat(comments.get(3).getContent()).isEqualTo("대댓글1");
	}

}
