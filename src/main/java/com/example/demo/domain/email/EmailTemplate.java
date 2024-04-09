package com.example.demo.domain.email;

import lombok.Getter;

@Getter
public enum EmailTemplate {

	STUDYONCE_LOCATION_CHANGED("[Cafegory] 카페 장소가 변경되었습니다.", "자세한 내용은 마이페이지를 확인 해 주세요.");

	EmailTemplate(String subject, String content) {
		this.subject = subject;
		this.content = content;
	}

	private final String subject;
	private final String content;

}
