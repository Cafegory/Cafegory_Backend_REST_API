package com.example.demo.repository.study;

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

import com.example.demo.domain.study.StudyOnce;
import com.example.demo.dto.study.StudyOnceSearchRequest;
import com.example.demo.dto.study.TalkAbleState;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InMemoryStudyOnceRepository implements StudyOnceRepository {
	private final List<StudyOnce> memory = new ArrayList<>();
	public static final InMemoryStudyOnceRepository INSTANCE = new InMemoryStudyOnceRepository();

	private StudyOnce makeStudyOnceWithId(StudyOnce studyOnce) {
		return StudyOnce.builder()
			.id(count() + 1)
			.startDateTime(studyOnce.getStartDateTime())
			.endDateTime(studyOnce.getEndDateTime())
			.leader(studyOnce.getLeader())
			.name(studyOnce.getName())
			.nowMemberCount(studyOnce.getNowMemberCount())
			.maxMemberCount(studyOnce.getMaxMemberCount())
			.ableToTalk(studyOnce.isAbleToTalk())
			.cafe(studyOnce.getCafe())
			.isEnd(studyOnce.isEnd())
			.build();
	}

	@Override
	public boolean existsByLeaderId(Long leaderId) {
		return !findByLeaderId(leaderId).isEmpty();
	}

	@Override
	public List<StudyOnce> findByLeaderId(Long leaderId) {
		return memory.stream()
			.filter(studyOnce -> leaderId.equals(studyOnce.getLeader().getId()))
			.collect(Collectors.toList());
	}

	@Override
	public List<StudyOnce> findAllByStudyOnceSearchRequest(StudyOnceSearchRequest studyOnceSearchRequest) {
		List<StudyOnce> all = findAll();
		return all.stream()
			.filter(studyOnce -> {
				String area = studyOnceSearchRequest.getArea();
				TalkAbleState canTalk = studyOnceSearchRequest.getCanTalk();
				boolean onlyJoinAble = studyOnceSearchRequest.isOnlyJoinAble();
				int maxMemberCount = studyOnceSearchRequest.getMaxMemberCount();

				boolean inRegion = studyOnce.getCafe().getAddress().isInRegion(area);
				boolean calculateTalkAbleCondition = calculateTalkAbleCondition(canTalk, studyOnce);
				boolean calculateOnlyJoinCondition =
					!onlyJoinAble || studyOnce.canJoin(LocalDateTime.now().plusHours(3));
				boolean calculateMaxMemberCountCondition = studyOnce.getMaxMemberCount() <= maxMemberCount;
				return inRegion && calculateTalkAbleCondition && calculateOnlyJoinCondition
					&& calculateMaxMemberCountCondition;
			})
			.collect(Collectors.toList());
	}

	private boolean calculateTalkAbleCondition(TalkAbleState talkAbleState, StudyOnce studyOnce) {
		switch (talkAbleState) {
			case BOTH:
				return true;
			case YES:
				return studyOnce.isAbleToTalk();
			case NO:
				return !studyOnce.isAbleToTalk();
		}
		return false;
	}

	@Override
	public Long count(StudyOnceSearchRequest studyOnceSearchRequest) {
		return (long)findAllByStudyOnceSearchRequest(studyOnceSearchRequest).size();
	}

	@Override
	public List<StudyOnce> findAll() {
		return List.copyOf(memory);
	}

	@Override
	public List<StudyOnce> findAll(Sort sort) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public Page<StudyOnce> findAll(Pageable pageable) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public List<StudyOnce> findAllById(Iterable<Long> longs) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public long count() {
		return memory.size();
	}

	@Override
	public void deleteById(Long aLong) {
		memory.stream()
			.filter(studyOnce -> aLong.equals(studyOnce.getId()))
			.findFirst()
			.ifPresent(memory::remove);
	}

	@Override
	public void delete(StudyOnce entity) {
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
	public void deleteAll(Iterable<? extends StudyOnce> entities) {
		for (StudyOnce entity : entities) {
			delete(entity);
		}
	}

	@Override
	public void deleteAll() {
		memory.clear();
	}

	@Override
	public <S extends StudyOnce> S save(S entity) {
		if (entity.getId() != null) {
			delete(entity);
		}
		StudyOnce studyOnce = makeStudyOnceWithId(entity);
		memory.add(studyOnce);
		return (S)studyOnce;
	}

	@Override
	public <S extends StudyOnce> List<S> saveAll(Iterable<S> entities) {
		List<S> result = new ArrayList<>();
		for (S entity : entities) {
			result.add(save(entity));
		}
		return result;
	}

	@Override
	public Optional<StudyOnce> findById(@NonNull Long aLong) {
		return memory.stream()
			.filter(studyOnce -> aLong.equals(studyOnce.getId()))
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
	public <S extends StudyOnce> S saveAndFlush(S entity) {
		return save(entity);
	}

	@Override
	public <S extends StudyOnce> List<S> saveAllAndFlush(Iterable<S> entities) {
		return saveAll(entities);
	}

	@Override
	public void deleteAllInBatch(Iterable<StudyOnce> entities) {
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
	public StudyOnce getOne(Long aLong) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public StudyOnce getById(Long aLong) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public StudyOnce getReferenceById(Long aLong) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public <S extends StudyOnce> Optional<S> findOne(Example<S> example) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public <S extends StudyOnce> List<S> findAll(Example<S> example) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public <S extends StudyOnce> List<S> findAll(Example<S> example, Sort sort) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public <S extends StudyOnce> Page<S> findAll(Example<S> example, Pageable pageable) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public <S extends StudyOnce> long count(Example<S> example) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public <S extends StudyOnce> boolean exists(Example<S> example) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}

	@Override
	public <S extends StudyOnce, R> R findBy(Example<S> example,
		Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
		throw new UnsupportedOperationException("필요하면 구현하세요!");
	}
}
