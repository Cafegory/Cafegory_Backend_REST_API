package com.example.demo.repository.review;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import com.example.demo.domain.review.Review;

import lombok.NonNull;

public class InMemoryReviewRepository implements ReviewRepository {
	private final List<Review> reviews = new ArrayList<>();

	private Review makeReviewWitId(Review review) {
		return Review.builder()
			.id(count() + 1)
			.content(review.getContent())
			.member(review.getMember())
			.rate(review.getRate())
			.cafe(review.getCafe())
			.build();
	}

	@Override
	public List<Review> findAllByCafeId(@NonNull Long cafeId) {
		return reviews.stream()
			.filter(review -> cafeId.equals(review.getCafe().getId()))
			.collect(Collectors.toList());
	}

	@Override
	public Page<Review> findAllWithPagingByCafeId(Long cafeId, Pageable pageable) {
		List<Review> collect = reviews.stream()
			.filter(review -> cafeId.equals(review.getCafe().getId()))
			.skip(pageable.getOffset())
			.limit(pageable.getPageNumber())
			.collect(Collectors.toList());
		return new PageImpl<>(collect);
	}

	@Override
	public List<Review> findAll() {
		return List.copyOf(reviews);
	}

	@Override
	public List<Review> findAll(Sort sort) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public Page<Review> findAll(Pageable pageable) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public List<Review> findAllById(Iterable<Long> longs) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public long count() {
		return reviews.size();
	}

	@Override
	public void deleteById(@NonNull Long aLong) {
		reviews.stream()
			.filter(review -> aLong.equals(review.getId()))
			.findFirst()
			.ifPresent(reviews::remove);
	}

	@Override
	public void delete(Review entity) {
		if (entity.getId() != null) {
			deleteById(entity.getId());
		}
	}

	@Override
	public void deleteAllById(Iterable<? extends Long> longs) {
		for (Long aLong : longs) {
			deleteById(aLong);
		}
	}

	@Override
	public void deleteAll(Iterable<? extends Review> entities) {
		for (Review entity : entities) {
			delete(entity);
		}
	}

	@Override
	public void deleteAll() {
		reviews.clear();
	}

	@Override
	public <S extends Review> S save(S entity) {
		if (entity.getId() != null) {
			delete(entity);
		}
		Review review = makeReviewWitId(entity);
		reviews.add(review);
		return (S)review;
	}

	@Override
	public <S extends Review> List<S> saveAll(Iterable<S> entities) {
		List<S> result = new ArrayList<>();
		for (S entity : entities) {
			result.add(save(entity));
		}
		return result;
	}

	@Override
	public Optional<Review> findById(@NonNull Long aLong) {
		return reviews.stream()
			.filter(review -> aLong.equals(review.getId()))
			.findFirst();
	}

	@Override
	public boolean existsById(Long aLong) {
		return findById(aLong).isPresent();
	}

	@Override
	public void flush() {

	}

	@Override
	public <S extends Review> S saveAndFlush(S entity) {
		return save(entity);
	}

	@Override
	public <S extends Review> List<S> saveAllAndFlush(Iterable<S> entities) {
		return saveAll(entities);
	}

	@Override
	public void deleteAllInBatch(Iterable<Review> entities) {
		deleteAll(entities);
	}

	@Override
	public void deleteAllByIdInBatch(Iterable<Long> longs) {
		deleteAllById(longs);
	}

	@Override
	public void deleteAllInBatch() {
		deleteAll();
	}

	@Override
	public Review getOne(Long aLong) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public Review getById(Long aLong) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public Review getReferenceById(Long aLong) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public <S extends Review> Optional<S> findOne(Example<S> example) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public <S extends Review> List<S> findAll(Example<S> example) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public <S extends Review> List<S> findAll(Example<S> example, Sort sort) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public <S extends Review> Page<S> findAll(Example<S> example, Pageable pageable) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public <S extends Review> long count(Example<S> example) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public <S extends Review> boolean exists(Example<S> example) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public <S extends Review, R> R findBy(Example<S> example,
		Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}
}
