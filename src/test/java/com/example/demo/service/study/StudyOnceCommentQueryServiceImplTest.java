package com.example.demo.service.study;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.domain.cafe.Cafe;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.ThumbnailImage;
import com.example.demo.domain.study.StudyOnce;
import com.example.demo.domain.study.StudyOnceComment;
import com.example.demo.dto.study.StudyOnceCommentSearchListResponse;
import com.example.demo.dto.study.StudyOnceCommentSearchResponse;
import com.example.demo.dto.study.StudyOnceReplyResponse;
import com.example.demo.helper.CafeSaveHelper;
import com.example.demo.helper.MemberSaveHelper;
import com.example.demo.helper.StudyOnceCommentSaveHelper;
import com.example.demo.helper.StudyOnceSaveHelper;
import com.example.demo.helper.ThumbnailImageSaveHelper;
import com.example.demo.service.ServiceTest;

class StudyOnceCommentQueryServiceImplTest extends ServiceTest {

	@Autowired
	private StudyOnceCommentQueryService sut;
	@Autowired
	private CafeSaveHelper cafeSaveHelper;
	@Autowired
	private MemberSaveHelper memberSaveHelper;
	@Autowired
	private StudyOnceSaveHelper studyOnceSaveHelper;
	@Autowired
	private ThumbnailImageSaveHelper thumbnailImageSaveHelper;
	@Autowired
	private StudyOnceCommentSaveHelper studyOnceCommentSaveHelper;

	@Test
	@DisplayName("댓글과 대댓글을 조회한다.")
	void search_question_and_reply() {
		//given
		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		Member leader = memberSaveHelper.saveMember(thumbnailImage);
		Cafe cafe = cafeSaveHelper.saveCafe();
		StudyOnce studyOnce = studyOnceSaveHelper.saveStudyOnce(cafe, leader);
		Member member = memberSaveHelper.saveMember(thumbnailImage);
		StudyOnceComment question = studyOnceCommentSaveHelper.saveStudyOnceQuestionWithContent(member,
			studyOnce, "질문");
		studyOnceCommentSaveHelper.saveStudyOnceReplyWithContent(member,
			studyOnce, question, "답변");
		//when
		StudyOnceCommentSearchListResponse commentsSearchResponse = sut.searchSortedCommentsByStudyOnceId(
			studyOnce.getId());
		//then
		List<StudyOnceCommentSearchResponse> comments = commentsSearchResponse.getComments();
		StudyOnceCommentSearchResponse actualQuestion = comments.get(0);
		StudyOnceReplyResponse actualReply = actualQuestion.getReplies().get(0);
		assertAll(
			() -> assertThat(actualQuestion.getQuestionInfo().getComment()).isEqualTo("질문"),
			() -> assertThat(actualReply.getComment()).isEqualTo("답변")
		);
	}
}
