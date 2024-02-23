package com.example.demo.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.example.demo.helper.CafePersistHelper;
import com.example.demo.helper.MemberPersistHelper;
import com.example.demo.helper.ReviewPersistHelper;
import com.example.demo.helper.StudyMemberPersistHelper;
import com.example.demo.helper.StudyOncePersistHelper;
import com.example.demo.helper.ThumbnailImagePersistHelper;

@TestConfiguration
public class TestConfig {

	@Bean
	public ThumbnailImagePersistHelper thumbnailImagePersistHelper() {
		return new ThumbnailImagePersistHelper();
	}

	@Bean
	public MemberPersistHelper memberPersistHelper() {
		return new MemberPersistHelper();
	}

	@Bean
	public CafePersistHelper cafePersistHelper() {
		return new CafePersistHelper();
	}

	@Bean
	public ReviewPersistHelper reviewPersistHelper() {
		return new ReviewPersistHelper();
	}

	@Bean
	public StudyMemberPersistHelper studyMemberPersistHelper() {
		return new StudyMemberPersistHelper();
	}

	@Bean
	public StudyOncePersistHelper studyOncePersistHelper() {
		return new StudyOncePersistHelper();
	}
}
