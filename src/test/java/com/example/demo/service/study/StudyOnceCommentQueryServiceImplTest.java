package com.example.demo.service.study;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.demo.domain.cafe.Address;
import com.example.demo.domain.cafe.Cafe;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.ThumbnailImage;
import com.example.demo.dto.study.StudyOnceCommentRequest;
import com.example.demo.dto.study.StudyOnceCommentSearchResponse;
import com.example.demo.dto.study.StudyOnceCreateRequest;
import com.example.demo.dto.study.StudyOnceReplyResponse;
import com.example.demo.dto.study.StudyOnceSearchResponse;
import com.example.demo.mapper.MemberMapper;
import com.example.demo.mapper.StudyMemberMapper;
import com.example.demo.mapper.StudyOnceCommentMapper;
import com.example.demo.mapper.StudyOnceMapper;
import com.example.demo.repository.cafe.CafeRepository;
import com.example.demo.repository.cafe.InMemoryCafeRepository;
import com.example.demo.repository.member.InMemoryMemberRepository;
import com.example.demo.repository.member.MemberRepository;
import com.example.demo.repository.study.InMemoryStudyMemberRepository;
import com.example.demo.repository.study.InMemoryStudyOnceCommentRepository;
import com.example.demo.repository.study.InMemoryStudyOnceRepository;
import com.example.demo.repository.study.StudyOnceCommentRepository;
import com.example.demo.repository.study.StudyOnceRepository;

class StudyOnceCommentQueryServiceImplTest {

	private final StudyOnceCommentRepository studyOnceCommentRepository = new InMemoryStudyOnceCommentRepository();
	private final StudyOnceRepository studyOnceRepository = new InMemoryStudyOnceRepository();
	private final CafeRepository cafeRepository = new InMemoryCafeRepository();
	private final MemberRepository memberRepository = new InMemoryMemberRepository();
	private final InMemoryStudyMemberRepository studyMemberRepository = new InMemoryStudyMemberRepository();

	private final StudyOnceCommentService studyOnceCommentService = new StudyOnceCommentServiceImpl(
		studyOnceCommentRepository, memberRepository, studyOnceRepository);
	private final StudyOnceCommentQueryService studyOnceCommentQueryService = new StudyOnceCommentQueryServiceImpl(
		studyOnceCommentRepository, studyOnceRepository, new MemberMapper(), new StudyOnceCommentMapper());
	private final StudyOnceService studyOnceService = new StudyOnceServiceImpl(cafeRepository, studyOnceRepository,
		memberRepository,
		studyMemberRepository, new StudyOnceMapper(), new StudyMemberMapper());

	@Test
	@DisplayName("댓글,대댓글 목록 조회 기능 ")
	void searchCommentsSortedByStudyOnceId() {
		long leaderId = initMember();
		long studyOnceId = initStudyOnce(leaderId);
		StudyOnceCommentRequest questionRequest = new StudyOnceCommentRequest("질문");
		long memberId = initMember();
		Long questionId = studyOnceCommentService.saveQuestion(memberId, studyOnceId, questionRequest);
		StudyOnceCommentRequest replyRequest = new StudyOnceCommentRequest("답변");
		studyOnceCommentService.saveReply(leaderId, studyOnceId, questionId, replyRequest);

		var commentsSearchResponse = studyOnceCommentQueryService.searchSortedCommentsByStudyOnceId(studyOnceId);
		List<StudyOnceCommentSearchResponse> comments = commentsSearchResponse.getComments();
		StudyOnceCommentSearchResponse actualQuestion = comments.get(0);
		StudyOnceReplyResponse actualReply = actualQuestion.getReplies().get(0);

		Assertions.assertAll(
			() -> assertThat(actualQuestion.getQuestionWriter().getMemberId()).isEqualTo(memberId),
			() -> assertThat(actualQuestion.getQuestionInfo().getComment()).isEqualTo(questionRequest.getContent()),
			() -> assertThat(actualReply.getComment()).isEqualTo(replyRequest.getContent())
		);
	}

	private long initMember() {
		Member member = Member.builder()
			.name("테스트")
			.email("test@test.com")
			.thumbnailImage(ThumbnailImage.builder().thumbnailImage("testUrl").build())
			.build();
		member = memberRepository.save(member);
		return member.getId();
	}

	private long initStudyOnce(long leaderId) {
		LocalDateTime start = LocalDateTime.now().plusHours(4);
		LocalDateTime end = start.plusHours(4);
		long cafeId = initCafe();
		StudyOnceCreateRequest studyOnceCreateRequest = makeStudyOnceCreateRequest(start, end, cafeId);
		StudyOnceSearchResponse searchResponse = studyOnceService.createStudy(leaderId, studyOnceCreateRequest);
		return searchResponse.getStudyOnceId();
	}

	private long initCafe() {
		Address address = new Address("테스트도 테스트시 테스트구 테스트동 ...", "테스트동");
		Cafe cafe = Cafe.builder()
			.address(address)
			.name("테스트 카페")
			.build();
		cafe = cafeRepository.save(cafe);
		return cafe.getId();
	}

	private StudyOnceCreateRequest makeStudyOnceCreateRequest(LocalDateTime start, LocalDateTime end,
		long cafeId) {
		return new StudyOnceCreateRequest(cafeId, "테스트 스터디", start, end, 4, true);
	}
}
