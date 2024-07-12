package com.example.demo.helper;

import static com.example.demo.factory.TestBusinessHourFactory.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.cafe.BusinessHour;
import com.example.demo.domain.cafe.Cafe;
import com.example.demo.factory.TestBusinessHourFactory;
import com.example.demo.factory.TestCafeFactory;
import com.example.demo.repository.cafe.CafeRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
public class CafeSaveHelper {

	private final CafeRepository cafeRepository;

	public Cafe saveCafe() {
		Cafe cafe = TestCafeFactory.createCafe();
		return cafeRepository.save(cafe);
	}

	public Cafe saveCafeWith7daysFrom9To21() {
		Cafe cafe = TestCafeFactory.createCafe();
		List<BusinessHour> businessHours = createBusinessHoursWith7daysFrom9To21(cafe);
		cafe.changeBusinessHours(businessHours);
		return cafeRepository.save(cafe);
	}

	private List<BusinessHour> createBusinessHoursWith7daysFrom9To21(Cafe cafe) {
		List<BusinessHour> businessHours = new ArrayList<>();
		List<String> daysOfWeek = generateDaysOfWeek();
		for (String day : daysOfWeek) {
			businessHours.add(
				createBusinessHourWithDayAndTime(cafe, day, LocalTime.of(9, 0), LocalTime.of(21, 0))
			);
		}
		return businessHours;
	}

	public Cafe saveCafeWith24For7() {
		Cafe cafe = TestCafeFactory.createCafe();
		List<BusinessHour> businessHours = makeBusinessHourWith24For7(cafe);
		cafe.changeBusinessHours(businessHours);
		return cafeRepository.save(cafe);
	}

	private List<String> generateDaysOfWeek() {
		return List.of("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY");
	}

	private List<BusinessHour> makeBusinessHourWith24For7(Cafe cafe) {
		List<String> daysOfWeek = generateDaysOfWeek();
		List<BusinessHour> businessHours = new ArrayList<>();
		for (String day : daysOfWeek) {
			BusinessHour businessHour = TestBusinessHourFactory.createBusinessHourWithDayAnd24For7(cafe, day);
			businessHours.add(businessHour);
		}
		return businessHours;
	}
}
