package com.example.demo.dto.study;

import com.example.demo.dto.WriterResponse;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StudyOnceCommentResponse {

	private final Long commentId;
	private final String content;
	private final WriterResponse writer;
}
