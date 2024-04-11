package com.example.demo.repository.study;

import java.util.List;

import com.example.demo.domain.study.StudyOnce;
import com.example.demo.dto.study.StudyOnceSearchRequest;

public interface StudyOnceRepositoryCustom {
	List<StudyOnce> findAllByStudyOnceSearchRequest(StudyOnceSearchRequest studyOnceSearchRequest);

	Long count(StudyOnceSearchRequest studyOnceSearchRequest);
}
