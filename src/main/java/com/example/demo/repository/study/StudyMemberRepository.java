package com.example.demo.repository.study;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.study.StudyMember;
import com.example.demo.domain.study.StudyMemberId;

public interface StudyMemberRepository extends JpaRepository<StudyMember, StudyMemberId>, StudyMemberRepositoryCustom {
}
