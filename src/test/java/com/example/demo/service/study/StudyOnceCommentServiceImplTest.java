package com.example.demo.service.study;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.demo.domain.cafe.Cafe;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.ThumbnailImage;
import com.example.demo.domain.study.StudyOnce;
import com.example.demo.domain.study.StudyOnceComment;
import com.example.demo.dto.study.StudyOnceCommentRequest;
import com.example.demo.dto.study.StudyOnceCommentUpdateRequest;
import com.example.demo.exception.CafegoryException;
import com.example.demo.exception.ExceptionType;
import com.example.demo.helper.CafePersistHelper;
import com.example.demo.helper.CafePersistHelperImpl;
import com.example.demo.helper.MemberPersistHelper;
import com.example.demo.helper.MemberPersistHelperImpl;
import com.example.demo.helper.StudyOnceCommentPersistHelper;
import com.example.demo.helper.StudyOnceCommentPersistHelperImpl;
import com.example.demo.helper.StudyOncePersistHelper;
import com.example.demo.helper.StudyOncePersistHelperImpl;
import com.example.demo.repository.cafe.InMemoryCafeRepository;
import com.example.demo.repository.member.InMemoryMemberRepository;
import com.example.demo.repository.member.MemberRepository;
import com.example.demo.repository.study.InMemoryStudyOnceCommentRepository;
import com.example.demo.repository.study.InMemoryStudyOnceRepository;
import com.example.demo.repository.study.StudyOnceCommentRepository;
import com.example.demo.repository.study.StudyOnceRepository;

class StudyOnceCommentServiceImplTest {
	public static final ThumbnailImage THUMBNAIL_IMAGE = new ThumbnailImage(1L, "a");
	private final StudyOnceCommentRepository studyOnceCommentRepository = new InMemoryStudyOnceCommentRepository();
	private final MemberRepository memberRepository = new InMemoryMemberRepository();
	private final StudyOnceRepository studyOnceRepository = new InMemoryStudyOnceRepository();
	private final StudyOnceCommentService studyOnceCommentService = new StudyOnceCommentServiceImpl(
		studyOnceCommentRepository, memberRepository, studyOnceRepository);

	private final MemberPersistHelper memberPersistHelper = new MemberPersistHelperImpl(memberRepository);
	private final StudyOncePersistHelper studyOncePersistHelper = new StudyOncePersistHelperImpl(studyOnceRepository);
	private final CafePersistHelper cafePersistHelper = new CafePersistHelperImpl(new InMemoryCafeRepository());
	private final StudyOnceCommentPersistHelper studyOnceCommentPersistHelper = new StudyOnceCommentPersistHelperImpl(
		studyOnceCommentRepository);

	@Test
	@DisplayName("카공 질문을 저장한다.")
	void save_question() {
		//given
		Member leader = memberPersistHelper.persistMemberWithName(THUMBNAIL_IMAGE, "카공장");
		Member otherPerson = memberPersistHelper.persistMemberWithName(THUMBNAIL_IMAGE, "김동현");
		Cafe cafe = cafePersistHelper.persistDefaultCafe();
		StudyOnce studyOnce = studyOncePersistHelper.persistDefaultStudyOnce(cafe, leader);
		//when
		studyOnceCommentService.saveQuestion(otherPerson.getId(), studyOnce.getId(),
			new StudyOnceCommentRequest("몇시까지 공부하시나요?"));
		List<StudyOnceComment> questions = studyOnceCommentRepository.findAll();
		//then
		assertThat(questions.size()).isEqualTo(1);
	}

	@Test
	@DisplayName("카공장의 카공 질문을 저장한다.")
	void save_question_by_leader() {
		//given
		Member leader = memberPersistHelper.persistMemberWithName(THUMBNAIL_IMAGE, "카공장");
		Cafe cafe = cafePersistHelper.persistDefaultCafe();
		StudyOnce studyOnce = studyOncePersistHelper.persistDefaultStudyOnce(cafe, leader);
		//when
		studyOnceCommentService.saveQuestion(leader.getId(), studyOnce.getId(),
			new StudyOnceCommentRequest("카페 끝날때까지 공부합니다."));
		List<StudyOnceComment> questions = studyOnceCommentRepository.findAll();
		//then
		assertThat(questions.size()).isEqualTo(1);
	}

	@Test
	@DisplayName("카공 질문을 수정한다.")
	void update_question() {
		//given
		Member leader = memberPersistHelper.persistMemberWithName(THUMBNAIL_IMAGE, "카공장");
		Member otherPerson = memberPersistHelper.persistMemberWithName(THUMBNAIL_IMAGE, "김동현");
		Cafe cafe = cafePersistHelper.persistDefaultCafe();
		StudyOnce studyOnce = studyOncePersistHelper.persistDefaultStudyOnce(cafe, leader);
		StudyOnceComment question = studyOnceCommentPersistHelper.persistStudyOnceQuestionWithContent(
			otherPerson, studyOnce, "등록내용");
		//when
		studyOnceCommentService.updateQuestion(otherPerson.getId(), question.getId(),
			new StudyOnceCommentUpdateRequest("수정내용"));
		StudyOnceComment findQuestion = studyOnceCommentRepository.findById(question.getId()).get();
		//then
		assertThat(findQuestion.getContent()).isEqualTo("수정내용");
	}

	@Test
	@DisplayName("카공 질문은 질문한 회원 본인만 수정 할 수 있다.")
	void update_question_by_member_who_asked_the_question() {
		//given
		Member leader = memberPersistHelper.persistMemberWithName(THUMBNAIL_IMAGE, "카공장");
		Member otherPerson = memberPersistHelper.persistMemberWithName(THUMBNAIL_IMAGE, "김동현");
		Cafe cafe = cafePersistHelper.persistDefaultCafe();
		StudyOnce studyOnce = studyOncePersistHelper.persistDefaultStudyOnce(cafe, leader);
		StudyOnceComment question = studyOnceCommentPersistHelper.persistStudyOnceQuestionWithContent(
			otherPerson, studyOnce, "등록내용");
		//when
		assertDoesNotThrow(() ->
			studyOnceCommentService.updateQuestion(otherPerson.getId(), question.getId(),
				new StudyOnceCommentUpdateRequest("수정내용"))
		);
	}

	@Test
	@DisplayName("카공 질문 수정은 질문한 회원 본인이 아니라면 예외가 터진다.")
	void update_question_by_member_who_not_asked_the_question_exception() {
		//given
		Member leader = memberPersistHelper.persistMemberWithName(THUMBNAIL_IMAGE, "카공장");
		Member otherPerson = memberPersistHelper.persistMemberWithName(THUMBNAIL_IMAGE, "김동현");
		Cafe cafe = cafePersistHelper.persistDefaultCafe();
		StudyOnce studyOnce = studyOncePersistHelper.persistDefaultStudyOnce(cafe, leader);
		StudyOnceComment question = studyOnceCommentPersistHelper.persistStudyOnceQuestionWithContent(
			otherPerson, studyOnce, "등록내용");
		//when
		assertThatThrownBy(() ->
			studyOnceCommentService.updateQuestion(leader.getId(), question.getId(),
				new StudyOnceCommentUpdateRequest("수정내용"))
		).isInstanceOf(CafegoryException.class)
			.hasMessage(ExceptionType.STUDY_ONCE_COMMENT_PERMISSION_DENIED.getErrorMessage());
	}

	@Test
	@DisplayName("답변(대댓글)이 작성된 질문(댓글)을 수정 할 경우 예외가 터진다.")
	void update_question_when_reply_already_existed_then_exception() {
		//given
		Member leader = memberPersistHelper.persistMemberWithName(THUMBNAIL_IMAGE, "카공장");
		Member otherPerson = memberPersistHelper.persistMemberWithName(THUMBNAIL_IMAGE, "김동현");
		Cafe cafe = cafePersistHelper.persistDefaultCafe();
		StudyOnce studyOnce = studyOncePersistHelper.persistDefaultStudyOnce(cafe, leader);
		StudyOnceComment question = studyOnceCommentPersistHelper.persistStudyOnceQuestionWithContent(
			otherPerson, studyOnce, "댓글");
		StudyOnceComment studyOnceComment = studyOnceCommentPersistHelper.persistStudyOnceReplyWithContent(leader,
			studyOnce, question, "대댓글");

		//when
		assertThatThrownBy(() -> studyOnceCommentService.updateQuestion(otherPerson.getId(), question.getId(),
			new StudyOnceCommentUpdateRequest("수정내용")))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(ExceptionType.STUDY_ONCE_PARENT_COMMENT_MODIFICATION_BLOCKED.getErrorMessage());
	}

	@Test
	@DisplayName("카공 질문을 삭제한다.")
	void delete_question() {
		//given
		Member leader = memberPersistHelper.persistMemberWithName(THUMBNAIL_IMAGE, "카공장");
		Member otherPerson = memberPersistHelper.persistMemberWithName(THUMBNAIL_IMAGE, "김동현");
		Cafe cafe = cafePersistHelper.persistDefaultCafe();
		StudyOnce studyOnce = studyOncePersistHelper.persistDefaultStudyOnce(cafe, leader);
		StudyOnceComment question = studyOnceCommentPersistHelper.persistDefaultStudyOnceQuestion(
			otherPerson, studyOnce);
		//when
		studyOnceCommentService.deleteQuestion(question.getId());
		StudyOnceComment findQuestion = studyOnceCommentRepository.findById(question.getId()).orElse(null);
		//then
		assertThat(findQuestion).isNull();
	}

	@Test
	@DisplayName("답변(대댓글)이 작성된 질문(댓글)을 삭제 할 경우 예외가 터진다.")
	void delete_question_when_reply_already_existed_then_exception() {
		//given
		Member leader = memberPersistHelper.persistMemberWithName(THUMBNAIL_IMAGE, "카공장");
		Member otherPerson = memberPersistHelper.persistMemberWithName(THUMBNAIL_IMAGE, "김동현");
		Cafe cafe = cafePersistHelper.persistDefaultCafe();
		StudyOnce studyOnce = studyOncePersistHelper.persistDefaultStudyOnce(cafe, leader);
		StudyOnceComment question = studyOnceCommentPersistHelper.persistStudyOnceQuestionWithContent(
			otherPerson, studyOnce, "댓글");
		studyOnceCommentPersistHelper.persistStudyOnceReplyWithContent(leader,
			studyOnce, question, "대댓글");
		//when
		assertThatThrownBy(() -> studyOnceCommentService.deleteQuestion(question.getId()))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(ExceptionType.STUDY_ONCE_PARENT_COMMENT_REMOVAL_BLOCKED.getErrorMessage());
	}

	@Test
	@DisplayName("카공 답변(대댓글)을 생성한다.")
	void save_reply() {
		//given
		Member leader = memberPersistHelper.persistMemberWithName(THUMBNAIL_IMAGE, "카공장");
		Member otherPerson = memberPersistHelper.persistMemberWithName(THUMBNAIL_IMAGE, "김동현");
		Cafe cafe = cafePersistHelper.persistDefaultCafe();
		StudyOnce studyOnce = studyOncePersistHelper.persistDefaultStudyOnce(cafe, leader);
		StudyOnceComment question = studyOnceCommentPersistHelper.persistStudyOnceQuestionWithContent(
			otherPerson, studyOnce, "언제까지 공부하시나요?");
		//when
		Long savedReplyId = studyOnceCommentService.saveReply(leader.getId(), studyOnce.getId(), question.getId(),
			new StudyOnceCommentRequest("카페 끝날때까지 공부합니다."));
		StudyOnceComment savedReply = studyOnceCommentRepository.findById(savedReplyId).get();
		//then
		assertThat(savedReply.getParent().getId()).isEqualTo(question.getId());
	}

	@Test
	@DisplayName("카공장만이 질문에 답변을 할 수 있다.")
	void save_reply_by_leader() {
		//given
		Member leader = memberPersistHelper.persistMemberWithName(THUMBNAIL_IMAGE, "카공장");
		Member otherPerson = memberPersistHelper.persistMemberWithName(THUMBNAIL_IMAGE, "김동현");
		Cafe cafe = cafePersistHelper.persistDefaultCafe();
		StudyOnce studyOnce = studyOncePersistHelper.persistDefaultStudyOnce(cafe, leader);
		StudyOnceComment question = studyOnceCommentPersistHelper.persistStudyOnceQuestionWithContent(
			otherPerson, studyOnce, "언제까지 공부하시나요?");
		//then
		assertDoesNotThrow(() -> studyOnceCommentService.saveReply(leader.getId(), studyOnce.getId(), question.getId(),
			new StudyOnceCommentRequest("카페 끝날때까지 공부합니다.")));
	}

	@Test
	@DisplayName("카공장이 아닌 다른 사람이 질문을 할 경우, 예외가 터진다.")
	void save_reply_by_not_leader_then_exception() {
		//given
		Member leader = memberPersistHelper.persistMemberWithName(THUMBNAIL_IMAGE, "카공장");
		Member otherPerson = memberPersistHelper.persistMemberWithName(THUMBNAIL_IMAGE, "김동현");
		Cafe cafe = cafePersistHelper.persistDefaultCafe();
		StudyOnce studyOnce = studyOncePersistHelper.persistDefaultStudyOnce(cafe, leader);
		StudyOnceComment question = studyOnceCommentPersistHelper.persistStudyOnceQuestionWithContent(
			otherPerson, studyOnce, "언제까지 공부하시나요?");
		//then
		assertThatThrownBy(
			() -> studyOnceCommentService.saveReply(otherPerson.getId(), studyOnce.getId(), question.getId(),
				new StudyOnceCommentRequest("카페 끝날때까지 공부합니다.")))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(ExceptionType.STUDY_ONCE_REPLY_PERMISSION_DENIED.getErrorMessage());
	}

	@Test
	@DisplayName("한개의 질문(댓글)에 한개의 답변(대댓글)이 존재할때, 한개의 답변(대대댓글)을 생성하면 예외가 터진다.")
	void save_reply_second_times_then_exception() {
		//given
		Member leader = memberPersistHelper.persistMemberWithName(THUMBNAIL_IMAGE, "카공장");
		Member otherPerson = memberPersistHelper.persistMemberWithName(THUMBNAIL_IMAGE, "김동현");
		Cafe cafe = cafePersistHelper.persistDefaultCafe();
		StudyOnce studyOnce = studyOncePersistHelper.persistDefaultStudyOnce(cafe, leader);
		StudyOnceComment question = studyOnceCommentPersistHelper.persistStudyOnceQuestionWithContent(
			otherPerson, studyOnce, "언제까지 공부하시나요?");
		StudyOnceComment reply = studyOnceCommentPersistHelper.persistStudyOnceReplyWithContent(leader,
			studyOnce, question, "카페 끝날때 까지 공부할것 같아요");
		//then
		assertThatThrownBy(
			() -> studyOnceCommentService.saveReply(leader.getId(), studyOnce.getId(), reply.getId(),
				new StudyOnceCommentRequest("카페 23시까지 운영해요")))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(ExceptionType.STUDY_ONCE_SINGLE_REPLY_PER_QUESTION.getErrorMessage());
	}

	@Test
	@DisplayName("부모댓글이 없는 최상위 댓글이 자식댓글을 두개이상 가지면 예외가 터진다.")
	void save_reply_when_top_level_comment_try_to_have_two_replies() {
		//given
		Member leader = memberPersistHelper.persistMemberWithName(THUMBNAIL_IMAGE, "카공장");
		Member otherPerson = memberPersistHelper.persistMemberWithName(THUMBNAIL_IMAGE, "김동현");
		Cafe cafe = cafePersistHelper.persistDefaultCafe();
		StudyOnce studyOnce = studyOncePersistHelper.persistDefaultStudyOnce(cafe, leader);
		StudyOnceComment question = studyOnceCommentPersistHelper.persistStudyOnceQuestionWithContent(
			otherPerson, studyOnce, "언제까지 공부하시나요?");
		StudyOnceComment reply = studyOnceCommentPersistHelper.persistStudyOnceReplyWithContent(leader,
			studyOnce, question, "최상위 댓글을 참조로 가지는 첫번째 댓글");
		//then
		assertThatThrownBy(() -> studyOnceCommentService.saveReply(leader.getId(), studyOnce.getId(), question.getId(),
			new StudyOnceCommentRequest("최상위 댓글을 참조로 가지는 두번째 댓글")))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(ExceptionType.STUDY_ONCE_SINGLE_REPLY_PER_QUESTION.getErrorMessage());
	}

	@Test
	@DisplayName("답변(대댓글)을 수정한다.")
	void update_reply() {
		//given
		Member leader = memberPersistHelper.persistMemberWithName(THUMBNAIL_IMAGE, "카공장");
		Member otherPerson = memberPersistHelper.persistMemberWithName(THUMBNAIL_IMAGE, "김동현");
		Cafe cafe = cafePersistHelper.persistDefaultCafe();
		StudyOnce studyOnce = studyOncePersistHelper.persistDefaultStudyOnce(cafe, leader);
		StudyOnceComment question = studyOnceCommentPersistHelper.persistStudyOnceQuestionWithContent(
			otherPerson, studyOnce, "댓글");
		StudyOnceComment reply = studyOnceCommentPersistHelper.persistStudyOnceReplyWithContent(leader,
			studyOnce, question, "대댓글");
		//when
		studyOnceCommentService.updateReply(leader.getId(), reply.getId(),
			new StudyOnceCommentUpdateRequest("대댓글수정"));
		StudyOnceComment updatedComment = studyOnceCommentRepository.findById(reply.getId()).get();
		//then
		assertThat(updatedComment.getContent()).isEqualTo("대댓글수정");
	}

	@Test
	@DisplayName("답변(대댓글)을 삭제한다.")
	void remove_reply() {
		//given
		Member leader = memberPersistHelper.persistMemberWithName(THUMBNAIL_IMAGE, "카공장");
		Member otherPerson = memberPersistHelper.persistMemberWithName(THUMBNAIL_IMAGE, "김동현");
		Cafe cafe = cafePersistHelper.persistDefaultCafe();
		StudyOnce studyOnce = studyOncePersistHelper.persistDefaultStudyOnce(cafe, leader);
		StudyOnceComment question = studyOnceCommentPersistHelper.persistStudyOnceQuestionWithContent(
			otherPerson, studyOnce, "댓글");
		StudyOnceComment reply = studyOnceCommentPersistHelper.persistStudyOnceReplyWithContent(leader,
			studyOnce, question, "대댓글");
		//when
		studyOnceCommentService.deleteReply(reply.getId());
		StudyOnceComment removedComment = studyOnceCommentRepository.findById(reply.getId()).orElse(null);
		//then
		assertThat(removedComment).isNull();
	}

	@Test
	@DisplayName("카공 답변(대댓글) 수정은 답변한 회원 본인이 아니라면 예외가 터진다.")
	void update_reply_by_member_who_not_asked_the_reply_exception() {
		//given
		Member leader = memberPersistHelper.persistMemberWithName(THUMBNAIL_IMAGE, "카공장");
		Member otherPerson = memberPersistHelper.persistMemberWithName(THUMBNAIL_IMAGE, "김동현");
		Cafe cafe = cafePersistHelper.persistDefaultCafe();
		StudyOnce studyOnce = studyOncePersistHelper.persistDefaultStudyOnce(cafe, leader);
		StudyOnceComment question = studyOnceCommentPersistHelper.persistStudyOnceQuestionWithContent(
			otherPerson, studyOnce, "댓글");
		StudyOnceComment reply = studyOnceCommentPersistHelper.persistStudyOnceReplyWithContent(leader,
			studyOnce, question, "대댓글");
		//when
		assertThatThrownBy(() ->
			studyOnceCommentService.updateReply(otherPerson.getId(), reply.getId(),
				new StudyOnceCommentUpdateRequest("수정내용"))
		).isInstanceOf(CafegoryException.class)
			.hasMessage(ExceptionType.STUDY_ONCE_COMMENT_PERMISSION_DENIED.getErrorMessage());
	}

}
