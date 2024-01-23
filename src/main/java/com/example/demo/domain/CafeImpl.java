package com.example.demo.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;

	private String name;

	// private String address;
	// private String region;
	@Embedded
	private Address address;

	// private int minBeveragePrice;
	@Transient
	private Menu menu;

	@Transient
	private boolean isOpen;
	private String phone;

	// private int maxTime;
	private int maxAllowableStay;
	private double avgReviewRate;

	@OneToMany(mappedBy = "cafe")
	private List<BusinessHour> businessHours = new ArrayList<>();

	@OneToMany(mappedBy = "cafe")
	private List<SnsDetail> snsDetails = new ArrayList<>();

	@Override
	public int calcMinBeveragePrice() {
		return 0;
	}

	@Override
	public String showAddress() {
		// return address.showFullAddress();
		return null;
	}

	// @Override
	// public void addReview(Review review) {
	//
	// }

}