package com.example.demo.repository.study;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import com.example.demo.domain.member.Member;
import com.example.demo.domain.study.StudyMember;
import com.example.demo.domain.study.StudyMemberId;
import com.example.demo.domain.study.StudyOnce;

import lombok.NonNull;

public class InMemoryStudyMemberRepository implements StudyMemberRepository {
	private final List<StudyMember> studyMembers = new ArrayList<>();

	@Override
	public List<StudyMember> findByMemberAndStudyDate(Member member, LocalDate studyDate) {
		List<StudyMember> all = findAll();
		return all.stream()
			.filter(studyMember -> {
				Long memberId = studyMember.getMember().getId();
				return memberId.equals(member.getId());
			})
			.filter(studyMember -> {
				StudyOnce study = studyMember.getStudy();
				LocalDateTime startDateTime = study.getStartDateTime();
				boolean sameYear = startDateTime.getYear() == studyDate.getYear();
				boolean sameMonth = startDateTime.getMonth() == studyDate.getMonth();
				int dayOfMonth = studyDate.getDayOfMonth();
				boolean contains = List.of(dayOfMonth - 1, dayOfMonth, dayOfMonth + 1)
					.contains(startDateTime.getDayOfMonth());
				return sameYear && sameMonth && contains;
			})
			.collect(Collectors.toList());
	}

	@Override
	public List<StudyMember> findAll() {
		return List.copyOf(studyMembers);
	}

	@Override
	public List<StudyMember> findAll(Sort sort) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public Page<StudyMember> findAll(Pageable pageable) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public List<StudyMember> findAllById(Iterable<StudyMemberId> studyMemberIds) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public long count() {
		return studyMembers.size();
	}

	@Override
	public void deleteById(@NonNull StudyMemberId studyMemberId) {
		studyMembers.stream()
			.filter(studyMember -> studyMemberId.equals(studyMember.getId()))
			.findFirst()
			.ifPresent(studyMembers::remove);
	}

	@Override
	public void delete(StudyMember entity) {
		if (entity.getId() != null) {
			deleteById(entity.getId());
		}
	}

	@Override
	public void deleteAllById(Iterable<? extends StudyMemberId> studyMemberIds) {
		for (StudyMemberId studyMemberId : studyMemberIds) {
			deleteById(studyMemberId);
		}
	}

	@Override
	public void deleteAll(Iterable<? extends StudyMember> entities) {
		for (StudyMember entity : entities) {
			delete(entity);
		}
	}

	@Override
	public void deleteAll() {
		studyMembers.clear();
	}

	@Override
	public <S extends StudyMember> S save(S entity) {
		if (entity.getId() != null) {
			deleteById(entity.getId());
		}
		studyMembers.add(entity);
		return entity;
	}

	@Override
	public <S extends StudyMember> List<S> saveAll(Iterable<S> entities) {
		List<S> result = new ArrayList<>();
		for (S entity : entities) {
			result.add(save(entity));
		}
		return result;
	}

	@Override
	public Optional<StudyMember> findById(@NonNull StudyMemberId studyMemberId) {
		return studyMembers.stream()
			.filter(studyMember -> studyMemberId.equals(studyMember.getId()))
			.findFirst();
	}

	@Override
	public boolean existsById(StudyMemberId studyMemberId) {
		return findById(studyMemberId).isPresent();
	}

	@Override
	public void flush() {

	}

	@Override
	public <S extends StudyMember> S saveAndFlush(S entity) {
		return save(entity);
	}

	@Override
	public <S extends StudyMember> List<S> saveAllAndFlush(Iterable<S> entities) {
		return saveAll(entities);
	}

	@Override
	public void deleteAllInBatch(Iterable<StudyMember> entities) {
		deleteAll(entities);
	}

	@Override
	public void deleteAllByIdInBatch(Iterable<StudyMemberId> studyMemberIds) {
		deleteAllById(studyMemberIds);
	}

	@Override
	public void deleteAllInBatch() {
		deleteAll();
	}

	@Override
	public StudyMember getOne(StudyMemberId studyMemberId) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public StudyMember getById(StudyMemberId studyMemberId) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public StudyMember getReferenceById(StudyMemberId studyMemberId) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public <S extends StudyMember> Optional<S> findOne(Example<S> example) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public <S extends StudyMember> List<S> findAll(Example<S> example) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public <S extends StudyMember> List<S> findAll(Example<S> example, Sort sort) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public <S extends StudyMember> Page<S> findAll(Example<S> example, Pageable pageable) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public <S extends StudyMember> long count(Example<S> example) {
		return studyMembers.size();
	}

	@Override
	public <S extends StudyMember> boolean exists(Example<S> example) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public <S extends StudyMember, R> R findBy(Example<S> example,
		Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}
}
