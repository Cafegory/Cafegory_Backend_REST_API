package com.example.demo.repository.study;

import java.util.List;

import com.example.demo.domain.study.StudyOnceImpl;
import com.example.demo.dto.study.StudyOnceSearchRequest;

public interface StudyOnceRepositoryCustom {
	List<StudyOnceImpl> findAllByStudyOnceSearchRequest(StudyOnceSearchRequest studyOnceSearchRequest);

	Long count(StudyOnceSearchRequest studyOnceSearchRequest);
}
