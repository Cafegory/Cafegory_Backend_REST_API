package com.example.demo.domain.cafe;

import static com.example.demo.exception.ExceptionType.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

import com.example.demo.exception.CafegoryException;

import lombok.Getter;

@Getter
public class CafeSearchCondition {

	private final boolean isAbleToStudy;
	private final String region;
	private final MaxAllowableStay maxAllowableStay;
	private final MinMenuPrice minMenuPrice;
	private final LocalTime startTime;
	private final LocalTime endTime;
	private final LocalDateTime now;

	private CafeSearchCondition(Builder builder) {
		this.isAbleToStudy = builder.isAbleToStudy;
		this.region = builder.region;
		this.maxAllowableStay = builder.maxAllowableStay;
		this.minMenuPrice = builder.minMenuPrice;
		this.startTime = builder.startTime;
		this.endTime = builder.endTime;
		this.now = builder.now;
	}

	public static class Builder {

		private static final int START_TIME = 0;
		private static final int END_TIME = 24;
		private final boolean isAbleToStudy;
		private final String region;
		private MaxAllowableStay maxAllowableStay;
		private MinMenuPrice minMenuPrice;
		private LocalTime startTime;
		private LocalTime endTime;
		private LocalDateTime now = LocalDateTime.now();

		public Builder(boolean isAbleToStudy, String region) {
			this.isAbleToStudy = isAbleToStudy;
			this.region = region;
		}

		public Builder maxTime(int maxTime) {
			this.maxAllowableStay = MaxAllowableStay.find(maxTime);
			return this;
		}

		public Builder minMenuPrice(int value) {
			this.minMenuPrice = MinMenuPrice.find(value);
			return this;
		}

		public Builder startTime(int startTime) {
			validateTimeRange(startTime);
			this.startTime = calcLocalTime(startTime);
			return this;
		}

		public Builder endTime(int endTime) {
			validateTimeRange(endTime);
			this.endTime = calcLocalTime(endTime);
			return this;
		}

		public Builder now(LocalDateTime now) {
			this.now = now;
			return this;
		}

		public CafeSearchCondition build() {
			return new CafeSearchCondition(this);
		}

		private LocalTime calcLocalTime(int time) {
			if (time == END_TIME) {
				return LocalTime.MAX;
			}
			return LocalTime.of(time, 0);
		}

		private void validateTimeRange(int time) {
			if (!(time >= START_TIME && time <= END_TIME)) {
				throw new CafegoryException(CAFE_INVALID_BUSINESS_TIME_RANGE);
			}
		}
	}

}
