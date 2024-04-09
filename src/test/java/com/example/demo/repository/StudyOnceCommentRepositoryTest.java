package com.example.demo.repository;

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
import com.example.demo.domain.MemberImpl;
import com.example.demo.domain.StudyOnceComment;
import com.example.demo.domain.StudyOnceImpl;
import com.example.demo.domain.ThumbnailImage;
import com.example.demo.domain.cafe.CafeImpl;
import com.example.demo.helper.CafePersistHelper;
import com.example.demo.helper.MemberPersistHelper;
import com.example.demo.helper.StudyOnceCommentPersistHelper;
import com.example.demo.helper.StudyOncePersistHelper;
import com.example.demo.helper.ThumbnailImagePersistHelper;

@DataJpaTest
@Import({TestConfig.class, QueryDslConfig.class})
@Transactional
class StudyOnceCommentRepositoryTest {

	@Autowired
	private StudyOnceCommentRepository studyOnceCommentRepository;
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
	@DisplayName("studyOnceCommentId로 오름차순 정렬된, studyOnceId로 댓글목록 조회")
	void findAllByStudyOnceId() {
		//given
		ThumbnailImage thumb = thumbnailImagePersistHelper.persistDefaultThumbnailImage();
		MemberImpl leader = memberPersistHelper.persistMemberWithName(thumb, "카공장");
		MemberImpl otherPerson = memberPersistHelper.persistMemberWithName(thumb, "김동현");
		CafeImpl cafe = cafePersistHelper.persistDefaultCafe();
		StudyOnceImpl studyOnce = studyOncePersistHelper.persistDefaultStudyOnce(cafe, leader);
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
