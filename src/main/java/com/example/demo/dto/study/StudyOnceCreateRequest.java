package com.example.demo.dto.study;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyOnceCreateRequest {
	private long cafeId;
	private String name;
	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;
	private int maxMemberCount;
	private boolean canTalk;
}

