package com.example.demo.dto.study;

import javax.validation.constraints.NotBlank;

import lombok.Getter;

@Getter
public class StudyOnceCommentRequest {

	@NotBlank
	private String content;

	public StudyOnceCommentRequest() {
	}

	public StudyOnceCommentRequest(String content) {
		this.content = content;
	}
}
