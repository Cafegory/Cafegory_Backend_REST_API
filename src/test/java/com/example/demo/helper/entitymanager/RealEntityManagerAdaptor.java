package com.example.demo.helper.entitymanager;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class RealEntityManagerAdaptor<T> implements EntityManagerForPersistHelper<T> {
	@PersistenceContext
	private EntityManager em;

	@Override
	public T save(T entity) {
		em.persist(entity);
		return entity;
	}
}
