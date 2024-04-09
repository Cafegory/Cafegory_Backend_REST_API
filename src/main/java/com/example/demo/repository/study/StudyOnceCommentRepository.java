package com.example.demo.repository.study;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.domain.study.StudyOnceComment;

public interface StudyOnceCommentRepository extends JpaRepository<StudyOnceComment, Long> {

	@Query(value = "select c from StudyOnceComment c"
		+ " left join fetch c.parent"
		+ " left join fetch c.member"
		+ " where c.studyOnce.id = :studyOnceId"
		+ " order by c.id asc")
	List<StudyOnceComment> findAllByStudyOnceId(@Param("studyOnceId") Long studyOnceId);
}
