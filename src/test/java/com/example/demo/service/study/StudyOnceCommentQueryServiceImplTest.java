package com.example.demo.service.study;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.config.TestConfig;
import com.example.demo.domain.cafe.Cafe;
import com.example.demo.domain.member.MemberImpl;
import com.example.demo.domain.member.ThumbnailImage;
import com.example.demo.domain.study.StudyOnceComment;
import com.example.demo.domain.study.StudyOnceImpl;
import com.example.demo.dto.member.MemberResponse;
import com.example.demo.dto.study.StudyOnceCommentSearchResponse;
import com.example.demo.dto.study.StudyOnceCommentsSearchResponse;
import com.example.demo.helper.CafePersistHelper;
import com.example.demo.helper.MemberPersistHelper;
import com.example.demo.helper.StudyOnceCommentPersistHelper;
import com.example.demo.helper.StudyOncePersistHelper;
import com.example.demo.helper.ThumbnailImagePersistHelper;

@SpringBootTest
@Import(TestConfig.class)
@Transactional
class StudyOnceCommentQueryServiceImplTest {

	@Autowired
	private StudyOnceCommentQueryService studyOnceCommentQueryService;
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
	@DisplayName("하나의 댓글에 하나의 답글만 가능하다. 댓글,대댓글 목록 조회 기능 ")
	void searchCommentsSortedByStudyOnceId() {
		//given
		ThumbnailImage thumb = thumbnailImagePersistHelper.persistDefaultThumbnailImage();
		MemberImpl leader = memberPersistHelper.persistMemberWithName(thumb, "카공장");
		MemberImpl otherPerson = memberPersistHelper.persistMemberWithName(thumb, "김동현");
		Cafe cafe = cafePersistHelper.persistDefaultCafe();
		StudyOnceImpl studyOnce = studyOncePersistHelper.persistDefaultStudyOnce(cafe, leader);
		StudyOnceComment question1 = studyOnceCommentPersistHelper.persistStudyOnceQuestionWithContent(
			otherPerson, studyOnce, "댓글1");
		StudyOnceComment question2 = studyOnceCommentPersistHelper.persistStudyOnceQuestionWithContent(
			otherPerson, studyOnce, "댓글2");
		studyOnceCommentPersistHelper.persistStudyOnceReplyWithContent(leader,
			studyOnce, question1, "대댓글1");
		studyOnceCommentPersistHelper.persistStudyOnceReplyWithContent(leader,
			studyOnce, question2, "대댓글2");
		em.flush();
		em.clear();
		//when
		StudyOnceCommentsSearchResponse response = studyOnceCommentQueryService.searchSortedCommentsByStudyOnceId(
			studyOnce.getId());

		MemberResponse replyWriter = response.getReplyWriter();
		List<StudyOnceCommentSearchResponse> parentComments = response.getComments();
		StudyOnceCommentSearchResponse firstParentComment = parentComments.get(0);
		StudyOnceCommentSearchResponse secondParentComment = parentComments.get(1);
		//then
		assertThat(replyWriter.getMemberId()).isEqualTo(leader.getId());
		assertThat(parentComments.size()).isEqualTo(2);
		assertThat(firstParentComment.getQuestionInfo().getComment()).isEqualTo("댓글1");
		assertThat(secondParentComment.getQuestionInfo().getComment()).isEqualTo("댓글2");
		assertThat(firstParentComment.getReplies().get(0).getComment()).isEqualTo("대댓글1");
		assertThat(secondParentComment.getReplies().get(0).getComment()).isEqualTo("대댓글2");
	}

}
