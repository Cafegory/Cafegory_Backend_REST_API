package com.example.demo.dto.cafe;

import com.example.demo.dto.PagedRequest;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CafeSearchRequest extends PagedRequest {
	private int startTime = 0;
	private int endTime = 24;
	private boolean canStudy;
	private int minBeveragePrice = 0;
	private int maxTime = 0;
	private String area;

}
