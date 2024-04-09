package com.example.demo.dto.study;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StudyOnceReplyResponse {

	private final Long commentId;
	private final String comment;
}
