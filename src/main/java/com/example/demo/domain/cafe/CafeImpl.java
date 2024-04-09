package com.example.demo.domain.cafe;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.example.demo.domain.StudyOnceImpl;
import com.example.demo.domain.review.ReviewImpl;

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
public class CafeImpl implements Cafe {

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

	@OneToMany(mappedBy = "cafe")
	@Builder.Default
	private List<BusinessHour> businessHours = new ArrayList<>();

	@OneToMany(mappedBy = "cafe")
	@Builder.Default
	private List<SnsDetail> snsDetails = new ArrayList<>();

	@OneToMany(mappedBy = "cafe")
	@Builder.Default
	private List<ReviewImpl> reviews = new ArrayList<>();

	@OneToMany(mappedBy = "cafe")
	@Builder.Default
	private List<Menu> menus = new ArrayList<>();

	@OneToMany(mappedBy = "cafe")
	@Builder.Default
	private List<StudyOnceImpl> studyOnceGroup = new ArrayList<>();

	@Override
	public String showFullAddress() {
		return address.showFullAddress();
	}

	@Override
	public String getRegion() {
		return this.address.getRegion();
	}

	public boolean isOpen(OpenChecker<BusinessHour> openChecker) {
		return openChecker.checkWithBusinessHours(this.businessHours, LocalDateTime.now());
	}

}
