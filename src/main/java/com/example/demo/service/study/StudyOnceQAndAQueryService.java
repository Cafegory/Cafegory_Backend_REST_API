package com.example.demo.service.study;

import com.example.demo.dto.study.StudyOnceCommentResponse;

public interface StudyOnceQAndAQueryService {

	StudyOnceCommentResponse searchComment(Long studyOnceCommentId);
}
