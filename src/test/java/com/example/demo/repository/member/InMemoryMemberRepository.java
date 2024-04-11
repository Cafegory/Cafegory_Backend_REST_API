package com.example.demo.repository.member;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import com.example.demo.domain.member.Member;

import lombok.NonNull;

public class InMemoryMemberRepository implements MemberRepository {
	private final List<Member> members = new ArrayList<>();

	private Member makeMemberWithId(Member member) {
		if (member.getId() != null) {
			return member;
		}
		return Member.builder()
			.id(count() + 1)
			.email(member.getEmail())
			.studyMembers(member.getStudyMembers())
			.name(member.getName())
			.introduction(member.getIntroduction())
			.thumbnailImage(member.getThumbnailImage())
			.build();
	}

	@Override
	public Optional<Member> findByEmail(@NonNull String email) {
		return members.stream()
			.filter(member -> email.equals(member.getEmail()))
			.findFirst();
	}

	@Override
	public List<Member> findAll() {
		return List.copyOf(members);
	}

	@Override
	public List<Member> findAll(Sort sort) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public Page<Member> findAll(Pageable pageable) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public List<Member> findAllById(Iterable<Long> longs) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public long count() {
		return members.size();
	}

	@Override
	public void deleteById(@NonNull Long aLong) {
		members.stream()
			.filter(member -> aLong.equals(member.getId()))
			.findFirst()
			.ifPresent(this::delete);
	}

	@Override
	public void delete(Member entity) {
		if (entity.getId() == null) {
			return;
		}
		deleteById(entity.getId());
	}

	@Override
	public void deleteAllById(Iterable<? extends Long> longs) {
		for (Long aLong : longs) {
			deleteById(aLong);
		}
	}

	@Override
	public void deleteAll(Iterable<? extends Member> entities) {
		for (Member entity : entities) {
			delete(entity);
		}
	}

	@Override
	public void deleteAll() {
		members.clear();
	}

	@Override
	public <S extends Member> S save(S entity) {
		if (entity.getId() != null) {
			deleteById(entity.getId());
		}
		Member member = makeMemberWithId(entity);
		members.add(member);
		return (S)member;
	}

	@Override
	public <S extends Member> List<S> saveAll(Iterable<S> entities) {
		List<S> result = new ArrayList<>();
		for (S entity : entities) {
			S save = save(entity);
			result.add(save);
		}
		return result;
	}

	@Override
	public Optional<Member> findById(@NonNull Long aLong) {
		return members.stream()
			.filter(member -> aLong.equals(member.getId()))
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
	public <S extends Member> S saveAndFlush(S entity) {
		return save(entity);
	}

	@Override
	public <S extends Member> List<S> saveAllAndFlush(Iterable<S> entities) {
		return saveAll(entities);
	}

	@Override
	public void deleteAllInBatch(Iterable<Member> entities) {
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
	public Member getOne(Long aLong) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public Member getById(Long aLong) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public Member getReferenceById(Long aLong) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public <S extends Member> Optional<S> findOne(Example<S> example) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public <S extends Member> List<S> findAll(Example<S> example) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public <S extends Member> List<S> findAll(Example<S> example, Sort sort) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public <S extends Member> Page<S> findAll(Example<S> example, Pageable pageable) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public <S extends Member> long count(Example<S> example) {
		return members.size();
	}

	@Override
	public <S extends Member> boolean exists(Example<S> example) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public <S extends Member, R> R findBy(Example<S> example,
		Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}
}
