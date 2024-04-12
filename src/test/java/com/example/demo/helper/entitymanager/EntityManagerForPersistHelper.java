package com.example.demo.helper.entitymanager;

@FunctionalInterface
public interface EntityManagerForPersistHelper<T> {
	T save(T entity);
}
