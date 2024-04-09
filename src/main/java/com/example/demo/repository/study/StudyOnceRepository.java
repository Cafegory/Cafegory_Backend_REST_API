package com.example.demo.repository.study;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.study.StudyOnceImpl;

public interface StudyOnceRepository extends JpaRepository<StudyOnceImpl, Long>, StudyOnceRepositoryCustom {

	boolean existsByLeaderId(Long leaderId);

	List<StudyOnceImpl> findByLeaderId(Long leaderId);
}
