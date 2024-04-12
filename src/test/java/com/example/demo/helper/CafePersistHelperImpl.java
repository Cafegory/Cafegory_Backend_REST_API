package com.example.demo.helper;

import com.example.demo.builder.TestCafeBuilder;
import com.example.demo.domain.cafe.Cafe;
import com.example.demo.repository.cafe.CafeRepository;
import com.example.demo.repository.cafe.InMemoryCafeRepository;

public class CafePersistHelperImpl extends CafePersistHelper {
	private final CafeRepository cafeRepository;

	public CafePersistHelperImpl() {
		this(InMemoryCafeRepository.INSTANCE);
	}

	public CafePersistHelperImpl(CafeRepository cafeRepository) {
		this.cafeRepository = cafeRepository;
	}

	@Override
	public Cafe persistDefaultCafe() {
		Cafe cafe = new TestCafeBuilder().build();
		return cafeRepository.save(cafe);
	}
}
