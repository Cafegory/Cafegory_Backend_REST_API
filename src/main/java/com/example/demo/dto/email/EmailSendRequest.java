package com.example.demo.dto.email;

import java.util.List;

import lombok.Getter;

@Getter
public class EmailSendRequest {

	private String messageType;
	private List<Long> memberIds;

	public EmailSendRequest() {
	}

	public EmailSendRequest(String messageType, List<Long> memberIds) {
		this.messageType = messageType;
		this.memberIds = memberIds;
	}
}
