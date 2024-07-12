package com.example.demo.domain.cafe;

import static com.example.demo.exception.ExceptionType.*;
import static com.example.demo.util.TruncatedTimeUtil.*;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.example.demo.domain.review.Review;
import com.example.demo.domain.study.StudyOnce;
import com.example.demo.exception.CafegoryException;

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
@Table(name = "cafe")
public class Cafe {

	@Id
	@GeneratedValue
	@Column(name = "cafe_id")
	private Long id;

	private String name;

	@Embedded
	private Address address;
	private String phone;

	@Enumerated(EnumType.STRING)
	private MaxAllowableStay maxAllowableStay;

	private double avgReviewRate;
	private boolean isAbleToStudy;
	private int minBeveragePrice;

	@OneToMany(mappedBy = "cafe", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<BusinessHour> businessHours = new ArrayList<>();

	@OneToMany(mappedBy = "cafe")
	@Builder.Default
	private List<SnsDetail> snsDetails = new ArrayList<>();

	@OneToMany(mappedBy = "cafe")
	@Builder.Default
	private List<Review> reviews = new ArrayList<>();

	@OneToMany(mappedBy = "cafe")
	@Builder.Default
	private List<Menu> menus = new ArrayList<>();

	@OneToMany(mappedBy = "cafe")
	@Builder.Default
	private List<StudyOnce> studyOnceGroup = new ArrayList<>();

	public String showFullAddress() {
		return address.showFullAddress();
	}

	public String getRegion() {
		return this.address.getRegion();
	}

	public boolean isOpen(OpenChecker<BusinessHour> openChecker) {
		return openChecker.checkWithBusinessHours(this.businessHours, LOCAL_DATE_TIME_NOW);
	}

	public OptionalDouble calcAverageRating() {
		return reviews.stream()
			.mapToDouble(Review::getRate)
			.average();
	}

	public BusinessHour findBusinessHour(DayOfWeek dayOfWeek) {
		return businessHours.stream()
			.filter(businessHour -> businessHour.matchesDayOfWeek(dayOfWeek))
			.findFirst()
			.orElseThrow(() -> new CafegoryException(CAFE_NOT_FOUND_DAY_OF_WEEK));
	}

	public void changeBusinessHours(List<BusinessHour> businessHours) {
		this.businessHours = businessHours;
	}
}
