package com.example.demo.repository.study;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.study.StudyOnce;

public interface StudyOnceRepository extends JpaRepository<StudyOnce, Long>, StudyOnceRepositoryCustom {

	boolean existsByLeaderId(Long leaderId);

	List<StudyOnce> findByLeaderId(Long leaderId);
}
