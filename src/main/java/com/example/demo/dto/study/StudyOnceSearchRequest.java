package com.example.demo.dto.study;

import com.example.demo.dto.PagedRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class StudyOnceSearchRequest extends PagedRequest { //ToDo 구현 필요
	private boolean onlyJoinAble = true;
	@NonNull
	private String area;
	private TalkAbleState canTalk = TalkAbleState.BOTH;
	private int maxMemberCount = 0;
}
