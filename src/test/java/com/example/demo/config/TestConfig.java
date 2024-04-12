package com.example.demo.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.example.demo.helper.CafePersistHelper;
import com.example.demo.helper.MemberPersistHelper;
import com.example.demo.helper.ReviewPersistHelper;
import com.example.demo.helper.StudyMemberPersistHelper;
import com.example.demo.helper.StudyOnceCommentPersistHelper;
import com.example.demo.helper.StudyOncePersistHelper;
import com.example.demo.helper.ThumbnailImagePersistHelper;
import com.example.demo.helper.entitymanager.RealEntityManagerAdaptor;

@TestConfiguration
public class TestConfig {

	@Bean
	public ThumbnailImagePersistHelper thumbnailImagePersistHelper() {
		return new ThumbnailImagePersistHelper(realEntityManagerAdaptor());
	}

	@Bean
	public MemberPersistHelper memberPersistHelper() {
		return new MemberPersistHelper(realEntityManagerAdaptor());
	}

	@Bean
	public CafePersistHelper cafePersistHelper() {
		return new CafePersistHelper(realEntityManagerAdaptor());
	}

	@Bean
	public ReviewPersistHelper reviewPersistHelper() {
		return new ReviewPersistHelper(realEntityManagerAdaptor());
	}

	@Bean
	public StudyMemberPersistHelper studyMemberPersistHelper() {
		return new StudyMemberPersistHelper(realEntityManagerAdaptor());
	}

	@Bean
	public StudyOncePersistHelper studyOncePersistHelper() {
		return new StudyOncePersistHelper(realEntityManagerAdaptor());
	}

	@Bean
	public StudyOnceCommentPersistHelper studyOnceCommentPersistHelper() {
		return new StudyOnceCommentPersistHelper(realEntityManagerAdaptor());
	}

	@Bean
	public RealEntityManagerAdaptor realEntityManagerAdaptor() {
		return new RealEntityManagerAdaptor();
	}
}
