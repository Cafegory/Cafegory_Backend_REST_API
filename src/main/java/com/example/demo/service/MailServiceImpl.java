package com.example.demo.service;

import static com.example.demo.exception.ExceptionType.*;

import org.springframework.stereotype.Service;

import com.example.demo.domain.EmailSender;
import com.example.demo.domain.EmailTemplate;
import com.example.demo.domain.MemberImpl;
import com.example.demo.dto.EmailSendRequest;
import com.example.demo.dto.EmailTemplateResponse;
import com.example.demo.exception.CafegoryException;
import com.example.demo.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

	private final EmailSender emailSender;
	private final MemberRepository memberRepository;

	@Override
	public EmailTemplateResponse sendEmail(EmailSendRequest request) {
		EmailTemplate template = EmailTemplate.valueOf(request.getMessageType());
		for (Long memberId : request.getMemberIds()) {
			emailSender.sendSimpleMessage(findMemberById(memberId).getEmail(), template.getSubject(),
				template.getContent());
		}
		return new EmailTemplateResponse(template.getSubject(), template.getContent());
	}

	private MemberImpl findMemberById(Long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new CafegoryException(MEMBER_NOT_FOUND));
	}
	
}