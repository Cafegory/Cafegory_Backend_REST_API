package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.demo.domain.auth.CafegoryTokenManager;
import com.example.demo.dto.email.EmailSendRequest;
import com.example.demo.dto.email.EmailTemplateResponse;
import com.example.demo.service.email.MailService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MailController {

	private final CafegoryTokenManager cafegoryTokenManager;
	private final MailService mailService;

	@PostMapping("/email")
	public ResponseEntity<EmailTemplateResponse> sendEmail(
		@RequestHeader("Authorization") String authorization, @RequestBody EmailSendRequest request) {
		cafegoryTokenManager.getIdentityId(authorization);
		EmailTemplateResponse response = mailService.sendEmail(request);
		return ResponseEntity.ok(response);
	}
}
