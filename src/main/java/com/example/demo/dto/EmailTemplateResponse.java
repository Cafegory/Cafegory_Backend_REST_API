package com.example.demo.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EmailTemplateResponse {

	private final String title;
	private final String content;
}
