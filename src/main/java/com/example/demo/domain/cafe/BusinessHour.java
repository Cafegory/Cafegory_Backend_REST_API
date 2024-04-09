package com.example.demo.domain.cafe;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "business_hour")
public class BusinessHour {

	@Id
	@GeneratedValue
	@Column(name = "business_hour_id")
	private Long id;

	@Column(name = "days_of_week")
	private String day;

	private LocalTime startTime;
	private LocalTime endTime;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cafe_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private CafeImpl cafe;

	public boolean existsMatchingDayOfWeek(LocalDateTime now) {
		try {
			return now.getDayOfWeek().equals(DayOfWeek.valueOf(day));
		} catch (IllegalArgumentException e) {
			return false;
		}
	}
}
