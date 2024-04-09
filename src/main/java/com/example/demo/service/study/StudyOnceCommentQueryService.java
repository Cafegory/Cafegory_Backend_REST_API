package com.example.demo.service.study;

import com.example.demo.dto.study.StudyOnceCommentsSearchResponse;

public interface StudyOnceCommentQueryService {

	StudyOnceCommentsSearchResponse searchSortedCommentsByStudyOnceId(Long studyOnceId);
}
