package com.example.demo.dto.study;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class StudyOnceJoinResult {
	private final LocalDateTime requestTime;
	private final boolean result;
}
