package com.example.demo.dto.study;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class StudyOnceSearchResponse {
	private long cafeId;
	private String area;
	private long studyOnceId;
	private String name;
	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;
	private int maxMemberCount;
	private int nowMemberCount;
	private boolean canTalk;
	private boolean canJoin;
	private boolean isEnd;
}

