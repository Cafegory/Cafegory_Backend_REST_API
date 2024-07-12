package com.example.demo.helper;

import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.member.Member;
import com.example.demo.domain.study.StudyOnce;
import com.example.demo.domain.study.StudyOnceComment;
import com.example.demo.factory.TestStudyOnceCommentFactory;
import com.example.demo.repository.member.MemberRepository;
import com.example.demo.repository.study.StudyOnceCommentRepository;
import com.example.demo.repository.study.StudyOnceRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
public class StudyOnceCommentSaveHelper {

	private final StudyOnceCommentRepository studyOnceCommentRepository;
	private final MemberRepository memberRepository;
	private final StudyOnceRepository studyOnceRepository;

	public StudyOnceComment saveStudyOnceQuestion(Member member, StudyOnce studyOnce) {
		Member mergedMember = memberRepository.save(member);
		StudyOnce mergedStudyOnce = studyOnceRepository.save(studyOnce);
		StudyOnceComment studyOnceComment = TestStudyOnceCommentFactory.createStudyOnceQuestion(mergedMember,
			mergedStudyOnce);
		return studyOnceCommentRepository.save(studyOnceComment);
	}

	public StudyOnceComment saveStudyOnceQuestionWithContent(Member member, StudyOnce studyOnce,
		String content) {
		Member mergedMember = memberRepository.save(member);
		StudyOnce mergedStudyOnce = studyOnceRepository.save(studyOnce);
		StudyOnceComment studyOnceComment = TestStudyOnceCommentFactory.createStudyOnceQuestionWithContent(mergedMember,
			mergedStudyOnce, content);
		return studyOnceCommentRepository.save(studyOnceComment);
	}

	public StudyOnceComment saveStudyOnceReplyWithContent(Member member, StudyOnce studyOnce,
		StudyOnceComment parent, String content) {
		Member mergedMember = memberRepository.save(member);
		StudyOnce mergedStudyOnce = studyOnceRepository.save(studyOnce);
		StudyOnceComment mergedParent = studyOnceCommentRepository.save(parent);
		StudyOnceComment reply = TestStudyOnceCommentFactory.createStudyOnceReplyWithContent(mergedMember,
			mergedStudyOnce, mergedParent,
			content);
		parent.getChildren().add(reply);
		return studyOnceCommentRepository.save(reply);
	}
}
