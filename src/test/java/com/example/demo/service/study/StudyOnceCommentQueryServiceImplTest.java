package com.example.demo.service.study;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.demo.domain.cafe.Cafe;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.ThumbnailImage;
import com.example.demo.domain.study.StudyOnce;
import com.example.demo.dto.study.StudyOnceCommentRequest;
import com.example.demo.dto.study.StudyOnceCommentSearchResponse;
import com.example.demo.dto.study.StudyOnceReplyResponse;
import com.example.demo.helper.CafePersistHelperImpl;
import com.example.demo.helper.MemberPersistHelperImpl;
import com.example.demo.helper.StudyOncePersistHelperImpl;
import com.example.demo.mapper.MemberMapper;
import com.example.demo.mapper.StudyOnceCommentMapper;
import com.example.demo.repository.member.InMemoryMemberRepository;
import com.example.demo.repository.member.MemberRepository;
import com.example.demo.repository.study.InMemoryStudyOnceCommentRepository;
import com.example.demo.repository.study.InMemoryStudyOnceRepository;
import com.example.demo.repository.study.StudyOnceCommentRepository;
import com.example.demo.repository.study.StudyOnceRepository;
import com.example.demo.service.ServiceTest;

class StudyOnceCommentQueryServiceImplTest extends ServiceTest {

	public static final ThumbnailImage THUMBNAIL_IMAGE = new ThumbnailImage(1L, "testUrl");
	private final StudyOnceCommentRepository studyOnceCommentRepository = InMemoryStudyOnceCommentRepository.INSTANCE;
	private final StudyOnceRepository studyOnceRepository = InMemoryStudyOnceRepository.INSTANCE;
	private final MemberRepository memberRepository = InMemoryMemberRepository.INSTANCE;

	private final StudyOnceCommentService studyOnceCommentService = new StudyOnceCommentServiceImpl(
		studyOnceCommentRepository, memberRepository, studyOnceRepository);
	private final StudyOnceCommentQueryService studyOnceCommentQueryService = new StudyOnceCommentQueryServiceImpl(
		studyOnceCommentRepository, studyOnceRepository, new MemberMapper(), new StudyOnceCommentMapper());
	private final MemberPersistHelperImpl memberPersistHelper = new MemberPersistHelperImpl();
	private final CafePersistHelperImpl cafePersistHelper = new CafePersistHelperImpl();
	private final StudyOncePersistHelperImpl studyOncePersistHelper = new StudyOncePersistHelperImpl();

	@Test
	@DisplayName("댓글,대댓글 목록 조회 기능 ")
	void searchCommentsSortedByStudyOnceId() {
		Member leader = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE);
		Cafe cafe = cafePersistHelper.persistDefaultCafe();
		StudyOnce studyOnce = studyOncePersistHelper.persistDefaultStudyOnce(cafe, leader);
		StudyOnceCommentRequest questionRequest = new StudyOnceCommentRequest("질문");
		Member member = memberPersistHelper.persistDefaultMember(THUMBNAIL_IMAGE);
		Long questionId = studyOnceCommentService.saveQuestion(member.getId(), studyOnce.getId(), questionRequest);
		StudyOnceCommentRequest replyRequest = new StudyOnceCommentRequest("답변");
		studyOnceCommentService.saveReply(leader.getId(), studyOnce.getId(), questionId, replyRequest);

		var commentsSearchResponse = studyOnceCommentQueryService.searchSortedCommentsByStudyOnceId(studyOnce.getId());
		List<StudyOnceCommentSearchResponse> comments = commentsSearchResponse.getComments();
		StudyOnceCommentSearchResponse actualQuestion = comments.get(0);
		StudyOnceReplyResponse actualReply = actualQuestion.getReplies().get(0);

		Assertions.assertAll(
			() -> assertThat(actualQuestion.getQuestionWriter().getMemberId()).isEqualTo(member.getId()),
			() -> assertThat(actualQuestion.getQuestionInfo().getComment()).isEqualTo(questionRequest.getContent()),
			() -> assertThat(actualReply.getComment()).isEqualTo(replyRequest.getContent())
		);
	}
}
