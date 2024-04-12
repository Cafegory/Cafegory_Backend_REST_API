package com.example.demo.dto.profile;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class ProfileResponse {
	private final String name;
	private final String thumbnailImg;
	private final String introduction;
}
