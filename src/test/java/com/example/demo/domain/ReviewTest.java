package com.example.demo.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.example.demo.exception.CafegoryException;

class ReviewTest {

	@Test
	@DisplayName("null 검증")
	void validateNull() {
		assertThatThrownBy(() -> ReviewImpl.builder().content(null).build())
			.isInstanceOf(CafegoryException.class);
	}

	@ParameterizedTest
	@ValueSource(strings = {"", " "})
	@DisplayName("빈값, 공백문자 검증")
	void validateEmptyOrWhitespace(String value) {
		assertThatThrownBy(() -> ReviewImpl.builder().content(value).build())
			.isInstanceOf(CafegoryException.class);
	}

	@ParameterizedTest
	@ValueSource(doubles = {0, 5})
	@DisplayName("평점 정상 범위 검증")
	void validateRateRange(double number) {
		assertDoesNotThrow(() -> ReviewImpl.builder().content("a").rate(number).build());
	}

	@ParameterizedTest
	@ValueSource(doubles = {-0.1, 5.1})
	@DisplayName("평점 예외 범위 검증")
	void validateRateRange_exception(double number) {
		assertThatThrownBy(() -> ReviewImpl.builder().content("a").rate(number).build())
			.isInstanceOf(CafegoryException.class);
	}

}