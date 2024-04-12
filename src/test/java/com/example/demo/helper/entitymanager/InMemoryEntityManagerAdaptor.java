package com.example.demo.helper.entitymanager;

import java.util.function.Function;

public class InMemoryEntityManagerAdaptor<T> implements EntityManagerForPersistHelper<T> {
	private final Function<T, T> function;

	public InMemoryEntityManagerAdaptor(Function<T, T> function) {
		this.function = function;
	}

	@Override
	public T save(T entity) {
		return function.apply(entity);
	}
}
