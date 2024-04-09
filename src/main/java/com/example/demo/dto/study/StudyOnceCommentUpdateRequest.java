package com.example.demo.dto.study;

import javax.validation.constraints.NotBlank;

import lombok.Getter;

@Getter
public class StudyOnceCommentUpdateRequest {

	@NotBlank
	private String content;

	public StudyOnceCommentUpdateRequest() {
	}

	public StudyOnceCommentUpdateRequest(String content) {
		this.content = content;
	}
}
