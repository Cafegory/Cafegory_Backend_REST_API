package com.example.demo.domain.email;

public interface EmailSender {

	void sendSimpleMessage(String to, String subject, String text);
}
