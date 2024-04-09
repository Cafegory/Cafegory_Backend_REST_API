package com.example.demo.service.email;

import com.example.demo.dto.email.EmailSendRequest;
import com.example.demo.dto.email.EmailTemplateResponse;

public interface MailService {

	EmailTemplateResponse sendEmail(EmailSendRequest request);
}
