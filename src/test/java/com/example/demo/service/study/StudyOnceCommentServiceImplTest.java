package com.example.demo.service.study;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.domain.cafe.Cafe;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.ThumbnailImage;
import com.example.demo.domain.study.StudyOnce;
import com.example.demo.domain.study.StudyOnceComment;
import com.example.demo.dto.study.StudyOnceCommentRequest;
import com.example.demo.dto.study.StudyOnceCommentUpdateRequest;
import com.example.demo.exception.CafegoryException;
import com.example.demo.exception.ExceptionType;
import com.example.demo.helper.CafeSaveHelper;
import com.example.demo.helper.MemberSaveHelper;
import com.example.demo.helper.StudyOnceCommentSaveHelper;
import com.example.demo.helper.StudyOnceSaveHelper;
import com.example.demo.helper.ThumbnailImageSaveHelper;
import com.example.demo.repository.study.StudyOnceCommentRepository;
import com.example.demo.service.ServiceTest;

class StudyOnceCommentServiceImplTest extends ServiceTest {

	@Autowired
	private StudyOnceCommentService sut;
	@Autowired
	private StudyOnceCommentRepository studyOnceCommentRepository;
	@Autowired
	private CafeSaveHelper cafePersistHelper;
	@Autowired
	private MemberSaveHelper memberSaveHelper;
	@Autowired
	private StudyOnceSaveHelper studyOnceSaveHelper;
	@Autowired
	private ThumbnailImageSaveHelper thumbnailImageSaveHelper;
	@Autowired
	private StudyOnceCommentSaveHelper studyOnceCommentSaveHelper;

	@Test
	@DisplayName("답변이 달리지 않은 질문은 수정 가능하다.")
	void question_without_replies_can_be_updated() {
		//given
		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		Member leader = memberSaveHelper.saveMember(thumbnailImage);
		Member member = memberSaveHelper.saveMember(thumbnailImage);
		Cafe cafe = cafePersistHelper.saveCafe();
		StudyOnce studyOnce = studyOnceSaveHelper.saveStudyOnce(cafe, leader);
		StudyOnceComment question = studyOnceCommentSaveHelper.saveStudyOnceQuestionWithContent(
			member, studyOnce, "질문");
		//when
		sut.updateQuestion(member.getId(), question.getId(),
			new StudyOnceCommentUpdateRequest("수정"));
		StudyOnceComment findQuestion = studyOnceCommentRepository.findById(question.getId()).get();
		//then
		assertThat(findQuestion.getContent()).isEqualTo("수정");
	}

	@Test
	@DisplayName("답변이 달린 질문은 수정할 수 없다.")
	void question_with_replies_can_not_be_updated() {
		//given
		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		Member leader = memberSaveHelper.saveMember(thumbnailImage);
		Member member = memberSaveHelper.saveMember(thumbnailImage);
		Cafe cafe = cafePersistHelper.saveCafe();
		StudyOnce studyOnce = studyOnceSaveHelper.saveStudyOnce(cafe, leader);
		StudyOnceComment question = studyOnceCommentSaveHelper.saveStudyOnceQuestionWithContent(
			member, studyOnce, "질문");
		studyOnceCommentSaveHelper.saveStudyOnceReplyWithContent(leader,
			studyOnce, question, "답변");
		//then
		assertThatThrownBy(() -> sut.updateQuestion(member.getId(), question.getId(),
			new StudyOnceCommentUpdateRequest("수정")))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(ExceptionType.STUDY_ONCE_PARENT_COMMENT_MODIFICATION_BLOCKED.getErrorMessage());
	}

	@Test
	@DisplayName("자신이 작성한 댓글만 수정 가능하다.")
	void member_can_update_own_comment_only() {
		//given
		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		Member leader = memberSaveHelper.saveMember(thumbnailImage);
		Member member = memberSaveHelper.saveMember(thumbnailImage);
		Cafe cafe = cafePersistHelper.saveCafe();
		StudyOnce studyOnce = studyOnceSaveHelper.saveStudyOnce(cafe, leader);
		StudyOnceComment comment = studyOnceCommentSaveHelper.saveStudyOnceQuestionWithContent(
			member, studyOnce, "댓글");
		//when
		sut.updateQuestion(member.getId(), comment.getId(),
			new StudyOnceCommentUpdateRequest("수정"));
		//then
		StudyOnceComment findComment = studyOnceCommentRepository.findById(comment.getId()).get();
		assertThat(findComment.getContent()).isEqualTo("수정");
	}

	@Test
	@DisplayName("다른 사람의 댓글을 수정할 수 없다.")
	void member_can_not_update_another_members_comment() {
		//given
		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		Member leader = memberSaveHelper.saveMember(thumbnailImage);
		Member member = memberSaveHelper.saveMember(thumbnailImage);
		Cafe cafe = cafePersistHelper.saveCafe();
		StudyOnce studyOnce = studyOnceSaveHelper.saveStudyOnce(cafe, leader);
		StudyOnceComment comment = studyOnceCommentSaveHelper.saveStudyOnceQuestionWithContent(
			member, studyOnce, "댓글");
		//then
		assertThatThrownBy(() ->
			sut.updateQuestion(leader.getId(), comment.getId(), new StudyOnceCommentUpdateRequest("수정"))
		).isInstanceOf(CafegoryException.class)
			.hasMessage(ExceptionType.STUDY_ONCE_COMMENT_PERMISSION_DENIED.getErrorMessage());
	}

	@Test
	@DisplayName("답변이 달리지 않은 질문은 삭제 가능하다.")
	void question_without_replies_can_be_deleted() {
		//given
		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		Member leader = memberSaveHelper.saveMember(thumbnailImage);
		Member member = memberSaveHelper.saveMember(thumbnailImage);
		Cafe cafe = cafePersistHelper.saveCafe();
		StudyOnce studyOnce = studyOnceSaveHelper.saveStudyOnce(cafe, leader);
		StudyOnceComment question = studyOnceCommentSaveHelper.saveStudyOnceQuestionWithContent(
			member, studyOnce, "질문");
		//when
		sut.deleteQuestion(question.getId());
		//then
		List<StudyOnceComment> comments = studyOnceCommentRepository.findAll();
		assertThat(comments.isEmpty()).isTrue();
	}

	@Test
	@DisplayName("답변이 달린 질문은 삭제할 수 없다.")
	void question_with_replies_can_not_be_deleted() {
		//given
		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		Member leader = memberSaveHelper.saveMember(thumbnailImage);
		Member member = memberSaveHelper.saveMember(thumbnailImage);
		Cafe cafe = cafePersistHelper.saveCafe();
		StudyOnce studyOnce = studyOnceSaveHelper.saveStudyOnce(cafe, leader);
		StudyOnceComment question = studyOnceCommentSaveHelper.saveStudyOnceQuestionWithContent(
			member, studyOnce, "질문");
		studyOnceCommentSaveHelper.saveStudyOnceReplyWithContent(leader,
			studyOnce, question, "답변");
		//then
		assertThatThrownBy(() -> sut.deleteQuestion(question.getId()))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(ExceptionType.STUDY_ONCE_PARENT_COMMENT_REMOVAL_BLOCKED.getErrorMessage());
	}

	@Test
	@DisplayName("카공장만 답변할 수 있다.")
	void only_leader_can_reply_to_questions() {
		//given
		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		Member leader = memberSaveHelper.saveMember(thumbnailImage);
		Member member1 = memberSaveHelper.saveMember(thumbnailImage);
		Member member2 = memberSaveHelper.saveMember(thumbnailImage);
		Cafe cafe = cafePersistHelper.saveCafe();
		StudyOnce studyOnce = studyOnceSaveHelper.saveStudyOnce(cafe, leader);
		StudyOnceComment question = studyOnceCommentSaveHelper.saveStudyOnceQuestionWithContent(
			member1, studyOnce, "질문");
		//then
		assertThatThrownBy(
			() -> sut.saveReply(member2.getId(), studyOnce.getId(), question.getId(),
				new StudyOnceCommentRequest("답변")))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(ExceptionType.STUDY_ONCE_REPLY_PERMISSION_DENIED.getErrorMessage());
	}

	@Test
	@DisplayName("한개의 질문에 한개의 답변만 존재한다.")
	void only_one_reply_allowed_per_question() {
		//given
		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		Member leader = memberSaveHelper.saveMember(thumbnailImage);
		Member member = memberSaveHelper.saveMember(thumbnailImage);
		Cafe cafe = cafePersistHelper.saveCafe();
		StudyOnce studyOnce = studyOnceSaveHelper.saveStudyOnce(cafe, leader);
		StudyOnceComment question = studyOnceCommentSaveHelper.saveStudyOnceQuestionWithContent(
			member, studyOnce, "질문1");
		StudyOnceComment reply = studyOnceCommentSaveHelper.saveStudyOnceReplyWithContent(leader,
			studyOnce, question, "답변");
		//then
		assertThatThrownBy(
			() -> sut.saveReply(leader.getId(), studyOnce.getId(), reply.getId(),
				new StudyOnceCommentRequest("질문2")))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(ExceptionType.STUDY_ONCE_SINGLE_REPLY_PER_QUESTION.getErrorMessage());
	}
}
