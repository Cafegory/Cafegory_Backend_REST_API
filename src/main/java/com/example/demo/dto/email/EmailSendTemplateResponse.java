package com.example.demo.dto.email;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EmailSendTemplateResponse {
	private final String title;
	private final String content;
}
