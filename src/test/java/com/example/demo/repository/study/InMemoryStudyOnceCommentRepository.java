package com.example.demo.repository.study;

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

import com.example.demo.domain.study.StudyOnceComment;

import lombok.NonNull;

public class InMemoryStudyOnceCommentRepository
	implements StudyOnceCommentRepository {
	private final List<StudyOnceComment> memory = new ArrayList<>();

	private StudyOnceComment makeStudyOnceCommentWithId(StudyOnceComment studyOnceComment) {
		if (studyOnceComment.getId() != null) {
			return studyOnceComment;
		}
		return StudyOnceComment.builder()
			.id(count() + 1)
			.studyOnce(studyOnceComment.getStudyOnce())
			.member(studyOnceComment.getMember())
			.content(studyOnceComment.getContent())
			.children(studyOnceComment.getChildren())
			.parent(studyOnceComment.getParent())
			.build();
	}

	@Override
	public List<StudyOnceComment> findAllByStudyOnceId(@NonNull Long studyOnceId) {
		return memory.stream()
			.filter(studyOnceComment -> studyOnceId.equals(studyOnceComment.getStudyOnce().getId()))
			.collect(Collectors.toList());
	}

	@Override
	public List<StudyOnceComment> findAll() {
		return List.copyOf(memory);
	}

	@Override
	public List<StudyOnceComment> findAll(Sort sort) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public Page<StudyOnceComment> findAll(Pageable pageable) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public List<StudyOnceComment> findAllById(Iterable<Long> longs) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public long count() {
		return memory.size();
	}

	@Override
	public void deleteById(@NonNull Long aLong) {
		memory.stream()
			.filter(studyOnceComment -> aLong.equals(studyOnceComment.getId()))
			.findFirst()
			.ifPresent(studyOnceComment -> memory.remove(studyOnceComment));
	}

	@Override
	public void delete(StudyOnceComment entity) {
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
	public void deleteAll(Iterable<? extends StudyOnceComment> entities) {
		for (StudyOnceComment entity : entities) {
			delete(entity);
		}
	}

	@Override
	public void deleteAll() {
		memory.clear();
	}

	@Override
	public <S extends StudyOnceComment> S save(S entity) {
		if (entity.getId() != null) {
			delete(entity);
		}
		StudyOnceComment studyOnceComment = makeStudyOnceCommentWithId(entity);
		memory.add(studyOnceComment);
		if (studyOnceComment.hasParentComment()) {
			addReply(studyOnceComment);
		}
		return (S)studyOnceComment;
	}

	private void addReply(StudyOnceComment studyOnceComment) {
		findById(studyOnceComment.getParent().getId())
			.ifPresent(parent -> parent.addReply(studyOnceComment));
	}

	@Override
	public <S extends StudyOnceComment> List<S> saveAll(Iterable<S> entities) {
		List<S> result = new ArrayList<>();
		for (S entity : entities) {
			result.add(save(entity));
		}
		return result;
	}

	@Override
	public Optional<StudyOnceComment> findById(@NonNull Long aLong) {
		return memory.stream()
			.filter(studyOnceComment -> aLong.equals(studyOnceComment.getId()))
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
	public <S extends StudyOnceComment> S saveAndFlush(S entity) {
		return save(entity);
	}

	@Override
	public <S extends StudyOnceComment> List<S> saveAllAndFlush(Iterable<S> entities) {
		return saveAll(entities);
	}

	@Override
	public void deleteAllInBatch(Iterable<StudyOnceComment> entities) {
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
	public StudyOnceComment getOne(Long aLong) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public StudyOnceComment getById(Long aLong) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public StudyOnceComment getReferenceById(Long aLong) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public <S extends StudyOnceComment> Optional<S> findOne(Example<S> example) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public <S extends StudyOnceComment> List<S> findAll(Example<S> example) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public <S extends StudyOnceComment> List<S> findAll(Example<S> example, Sort sort) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public <S extends StudyOnceComment> Page<S> findAll(Example<S> example, Pageable pageable) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public <S extends StudyOnceComment> long count(Example<S> example) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public <S extends StudyOnceComment> boolean exists(Example<S> example) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public <S extends StudyOnceComment, R> R findBy(Example<S> example,
		Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}
}
