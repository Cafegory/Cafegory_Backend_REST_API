package com.example.demo.helper;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.example.demo.builder.TestCafeBuilder;
import com.example.demo.domain.cafe.CafeImpl;

public class CafePersistHelper {

	@PersistenceContext
	private EntityManager em;

	public CafeImpl persistDefaultCafe() {
		CafeImpl cafe = new TestCafeBuilder().build();
		em.persist(cafe);
		return cafe;
	}

	public CafeImpl persistCafeWithImpossibleStudy() {
		CafeImpl cafe = new TestCafeBuilder().isNotAbleToStudy().build();
		em.persist(cafe);
		return cafe;
	}

}
