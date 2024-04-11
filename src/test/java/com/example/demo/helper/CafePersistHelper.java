package com.example.demo.helper;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.example.demo.builder.TestCafeBuilder;
import com.example.demo.domain.cafe.Cafe;

public class CafePersistHelper {

	@PersistenceContext
	private EntityManager em;

	public Cafe persistDefaultCafe() {
		Cafe cafe = new TestCafeBuilder().build();
		em.persist(cafe);
		return cafe;
	}

	public Cafe persistCafeWithImpossibleStudy() {
		Cafe cafe = new TestCafeBuilder().isNotAbleToStudy().build();
		em.persist(cafe);
		return cafe;
	}

}
