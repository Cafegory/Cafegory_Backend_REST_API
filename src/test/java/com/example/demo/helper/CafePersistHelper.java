package com.example.demo.helper;

import com.example.demo.builder.TestCafeBuilder;
import com.example.demo.domain.cafe.Cafe;
import com.example.demo.helper.entitymanager.EntityManagerForPersistHelper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CafePersistHelper {

	private final EntityManagerForPersistHelper<Cafe> em;

	public Cafe persistDefaultCafe() {
		Cafe cafe = new TestCafeBuilder().build();
		return em.save(cafe);
	}

	public Cafe persistCafeWithImpossibleStudy() {
		Cafe cafe = new TestCafeBuilder().isNotAbleToStudy().build();
		return em.save(cafe);
	}

}
