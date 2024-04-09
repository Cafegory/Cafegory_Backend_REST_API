package com.example.demo.builder;

import com.example.demo.domain.member.MemberImpl;
import com.example.demo.domain.member.ThumbnailImage;

public class TestMemberBuilder {

	private Long id;
	private String name = "김동현";
	private String email = "cafegory@gmail.com";

	private ThumbnailImage thumbnailImage;

	public TestMemberBuilder id(Long id) {
		this.id = id;
		return this;
	}

	public TestMemberBuilder name(String name) {
		this.name = name;
		return this;
	}

	public TestMemberBuilder email(String email) {
		this.email = email;
		return this;
	}

	public TestMemberBuilder thumbnailImage(ThumbnailImage thumbnailImage) {
		this.thumbnailImage = thumbnailImage;
		return this;
	}

	public MemberImpl build() {
		return MemberImpl.builder()
			.id(id)
			.name(name)
			.email(email)
			.thumbnailImage(thumbnailImage)
			.build();
	}
}
