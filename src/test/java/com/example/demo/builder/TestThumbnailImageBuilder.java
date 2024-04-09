package com.example.demo.builder;

import com.example.demo.domain.member.ThumbnailImage;

public class TestThumbnailImageBuilder {
	private Long id;
	private String thumbnailImage = "k.kakaocdn.net/dn/1G9kp/btsAot8liOn/8CWudi3uy07rvFNUkk3ER0/img_640x640.jpg";

	public TestThumbnailImageBuilder id(Long id) {
		this.id = id;
		return this;
	}

	public TestThumbnailImageBuilder thumbnailImage(String thumbnailImage) {
		this.thumbnailImage = thumbnailImage;
		return this;
	}

	public ThumbnailImage build() {
		return new ThumbnailImage(id, thumbnailImage);
	}

}
