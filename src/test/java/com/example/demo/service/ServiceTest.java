package com.example.demo.service;

import org.junit.jupiter.api.BeforeEach;

import com.example.demo.repository.cafe.InMemoryCafeRepository;
import com.example.demo.repository.member.InMemoryMemberRepository;
import com.example.demo.repository.review.InMemoryReviewRepository;
import com.example.demo.repository.study.InMemoryStudyMemberRepository;
import com.example.demo.repository.study.InMemoryStudyOnceCommentRepository;
import com.example.demo.repository.study.InMemoryStudyOnceRepository;

public class ServiceTest {
	@BeforeEach
	void clearRepository() {
		InMemoryCafeRepository.INSTANCE.deleteAll();
		InMemoryStudyOnceRepository.INSTANCE.deleteAll();
		InMemoryStudyOnceCommentRepository.INSTANCE.deleteAll();
		InMemoryStudyMemberRepository.INSTANCE.deleteAll();
		InMemoryMemberRepository.INSTANCE.deleteAll();
		InMemoryReviewRepository.INSTANCE.deleteAll();
	}
}
