package com.example.demo.repository.cafe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import com.example.demo.domain.cafe.Cafe;

import lombok.NonNull;

public class InMemoryCafeRepository implements CafeRepository {
	private final List<Cafe> cafes = new ArrayList<>();

	private Cafe makeCafeWithId(Cafe cafe) {
		if (cafe.getId() == null) {
			return Cafe.builder()
				.id((long)cafes.size() + 1)
				.menus(cafe.getMenus())
				.avgReviewRate(cafe.getAvgReviewRate())
				.businessHours(cafe.getBusinessHours())
				.phone(cafe.getPhone())
				.name(cafe.getName())
				.isAbleToStudy(cafe.isAbleToStudy())
				.address(cafe.getAddress())
				.maxAllowableStay(cafe.getMaxAllowableStay())
				.minBeveragePrice(cafe.getMinBeveragePrice())
				.reviews(cafe.getReviews())
				.snsDetails(cafe.getSnsDetails())
				.studyOnceGroup(cafe.getStudyOnceGroup())
				.build();
		}
		return cafe;
	}

	@Override
	public List<Cafe> findAll() {
		return List.copyOf(cafes);
	}

	@Override
	public List<Cafe> findAll(Sort sort) {
		return findAll();
	}

	@Override
	public Page<Cafe> findAll(Pageable pageable) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public List<Cafe> findAllById(Iterable<Long> longs) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public long count() {
		return cafes.size();
	}

	@Override
	public void deleteById(@NonNull Long aLong) {
		cafes.stream().filter(cafe -> aLong.equals(cafe.getId()))
			.findFirst()
			.ifPresent(cafes::remove);
	}

	@Override
	public void delete(Cafe entity) {
		cafes.remove(entity);
	}

	@Override
	public void deleteAllById(Iterable<? extends Long> longs) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public void deleteAll(Iterable<? extends Cafe> entities) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public void deleteAll() {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public <S extends Cafe> S save(S entity) {
		if (entity.getId() != null) {
			deleteById(entity.getId());
		}
		Cafe cafe = makeCafeWithId(entity);
		cafes.add(cafe);
		return (S)cafe;
	}

	@Override
	public <S extends Cafe> List<S> saveAll(Iterable<S> entities) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public Optional<Cafe> findById(Long aLong) {
		return cafes.stream()
			.filter(cafe -> aLong.equals(cafe.getId()))
			.findFirst();
	}

	@Override
	public boolean existsById(Long aLong) {
		return cafes.stream()
			.anyMatch(cafe -> aLong.equals(cafe.getId()));
	}

	@Override
	public void flush() {

	}

	@Override
	public <S extends Cafe> S saveAndFlush(S entity) {
		return save(entity);
	}

	@Override
	public <S extends Cafe> List<S> saveAllAndFlush(Iterable<S> entities) {
		return saveAll(entities);
	}

	@Override
	public void deleteAllInBatch(Iterable<Cafe> entities) {
		deleteAll(entities);
	}

	@Override
	public void deleteAllByIdInBatch(Iterable<Long> longs) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public void deleteAllInBatch() {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public Cafe getOne(Long aLong) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public Cafe getById(Long aLong) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public Cafe getReferenceById(Long aLong) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public <S extends Cafe> Optional<S> findOne(Example<S> example) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public <S extends Cafe> List<S> findAll(Example<S> example) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public <S extends Cafe> List<S> findAll(Example<S> example, Sort sort) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public <S extends Cafe> Page<S> findAll(Example<S> example, Pageable pageable) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public <S extends Cafe> long count(Example<S> example) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public <S extends Cafe> boolean exists(Example<S> example) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public <S extends Cafe, R> R findBy(Example<S> example,
		Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}
}
