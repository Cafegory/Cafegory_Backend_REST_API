package com.example.demo.domain.study;

import static com.example.demo.exception.ExceptionType.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.lang.NonNull;
import org.thymeleaf.util.StringUtils;

import com.example.demo.domain.cafe.Cafe;
import com.example.demo.domain.member.Member;
import com.example.demo.exception.CafegoryException;
import com.example.demo.exception.ExceptionType;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "study_once")
public class StudyOnce {

	private static final int LIMIT_MEMBER_CAPACITY = 5;

	@Id
	@GeneratedValue
	@Column(name = "study_once_id")
	private Long id;

	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cafe_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Cafe cafe;

	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;
	private int maxMemberCount;
	private int nowMemberCount;
	private boolean isEnd;
	private boolean ableToTalk;
	private String openChatUrl;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "leader_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Member leader;
	@OneToMany(mappedBy = "study", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	private List<StudyMember> studyMembers;

	@Builder
	private StudyOnce(Long id, String name, Cafe cafe, LocalDateTime startDateTime, LocalDateTime endDateTime,
		int maxMemberCount, int nowMemberCount, boolean isEnd, boolean ableToTalk, String openChatUrl, Member leader) {
		validateStartDateTime(startDateTime);
		validateStudyOnceTime(startDateTime, endDateTime);
		validateMaxMemberCount(maxMemberCount);
		this.id = id;
		validateEmptyOrWhiteSpace(name, STUDY_ONCE_NAME_EMPTY_OR_WHITESPACE);
		this.name = name;
		this.cafe = cafe;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
		validateNowMemberCountOverMaxLimit(nowMemberCount, maxMemberCount);
		this.maxMemberCount = maxMemberCount;
		this.nowMemberCount = nowMemberCount;
		this.isEnd = isEnd;
		this.ableToTalk = ableToTalk;
		validateEmptyOrWhiteSpace(openChatUrl, STUDY_ONCE_OPEN_CHAT_URL_EMPTY_OR_WHITESPACE);
		this.openChatUrl = openChatUrl;
		this.leader = leader;
		validateConflictJoin(leader);
		studyMembers = new ArrayList<>();
		studyMembers.add(new StudyMember(leader, this));
		this.nowMemberCount = 1;
	}

	private static void validateStartDateTime(LocalDateTime startDateTime) {
		LocalDateTime now = LocalDateTime.now();
		Duration between = Duration.between(now, startDateTime);
		if (between.toSeconds() < 3 * 60 * 60) {
			throw new CafegoryException(ExceptionType.STUDY_ONCE_WRONG_START_TIME);
		}
	}

	private static void validateStudyOnceTime(LocalDateTime startDateTime, LocalDateTime endDateTime) {
		Duration between = Duration.between(startDateTime, endDateTime);
		if (between.toSeconds() < 60 * 60) {
			throw new CafegoryException(STUDY_ONCE_SHORT_DURATION);
		}
		if (between.toSeconds() > 5 * 60 * 60) {
			throw new CafegoryException(STUDY_ONCE_LONG_DURATION);
		}
	}

	private void validateMaxMemberCount(int maxMemberCount) {
		if (maxMemberCount > LIMIT_MEMBER_CAPACITY) {
			throw new CafegoryException(STUDY_ONCE_LIMIT_MEMBER_CAPACITY);
		}
	}

	private void validateNowMemberCountOverMaxLimit(int nowMemberCount, int maxMemberCount) {
		if (nowMemberCount > maxMemberCount) {
			throw new CafegoryException(STUDY_ONCE_CANNOT_REDUCE_BELOW_CURRENT);
		}
	}

	public void tryJoin(Member memberThatExpectedToJoin, LocalDateTime requestTime) {
		validateJoinRequestTime(requestTime);
		validateDuplicateJoin(memberThatExpectedToJoin);
		validateConflictJoin(memberThatExpectedToJoin);
		validateStudyMemberIsFull();
		StudyMember studyMember = new StudyMember(memberThatExpectedToJoin, this);
		studyMembers.add(studyMember);
		memberThatExpectedToJoin.addStudyMember(studyMember);
		nowMemberCount = studyMembers.size();
	}

	private void validateStudyMemberIsFull() {
		if (nowMemberCount == maxMemberCount) {
			throw new CafegoryException(STUDY_ONCE_FULL);
		}
	}

	private void validateConflictJoin(Member memberThatExpectedToJoin) {
		boolean joinFail = memberThatExpectedToJoin.getStudyMembers()
			.stream()
			.anyMatch(studyMember -> studyMember.isConflictWith(startDateTime, endDateTime));
		if (joinFail) {
			throw new CafegoryException(STUDY_ONCE_CONFLICT_TIME);
		}
	}

	private void validateDuplicateJoin(Member memberThatExpectedToJoin) {
		boolean isAlreadyJoin = studyMembers.stream()
			.anyMatch(studyMember -> studyMember.getMember().equals(memberThatExpectedToJoin));
		if (isAlreadyJoin) {
			throw new CafegoryException(STUDY_ONCE_DUPLICATE);
		}
	}

	private void validateJoinRequestTime(LocalDateTime requestTime) {
		Duration between = Duration.between(requestTime, startDateTime);
		if (between.toSeconds() < 3600) {
			throw new CafegoryException(STUDY_ONCE_TOO_LATE_JOIN);
		}
	}

	public StudyMember tryQuit(Member memberThatExpectedToQuit, LocalDateTime requestTime) {
		validateQuitRequestTime(requestTime);
		StudyMember studyMember = studyMembers.stream()
			.filter(s -> s.getMember().getId().equals(memberThatExpectedToQuit.getId()))
			.findFirst()
			.orElseThrow(() -> new CafegoryException(STUDY_ONCE_TRY_QUIT_NOT_JOIN));
		studyMembers.remove(studyMember);
		return studyMember;
	}

	private void validateQuitRequestTime(LocalDateTime requestTime) {
		Duration between = Duration.between(requestTime, startDateTime);
		if (between.toSeconds() < 3600) {
			throw new CafegoryException(STUDY_ONCE_TOO_LATE_QUIT);
		}
	}

	public void changeCafe(@NonNull Cafe cafe) {
		this.cafe = cafe;
		cafe.getStudyOnceGroup().add(this);
	}

	public boolean isLeader(Member member) {
		return leader.getId().equals(member.getId());
	}

	public boolean canJoin(LocalDateTime baseDateTime) {
		Duration between = Duration.between(baseDateTime, startDateTime);
		return between.toSeconds() >= 60 * 60;
	}

	public void changeName(String name) {
		validateEmptyOrWhiteSpace(name, STUDY_ONCE_NAME_EMPTY_OR_WHITESPACE);
		this.name = name;
	}

	public void changeStudyOnceTime(LocalDateTime startDateTime, LocalDateTime endDateTime) {
		validateStartDateTime(startDateTime);
		validateStudyOnceTime(startDateTime, endDateTime);
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
	}

	public void changeMaxMemberCount(int maxMemberCount) {
		validateMaxMemberCount(maxMemberCount);
		validateNowMemberCountOverMaxLimit(this.nowMemberCount, maxMemberCount);
		this.maxMemberCount = maxMemberCount;
	}

	public void changeCanTalk(boolean ableToTalk) {
		this.ableToTalk = ableToTalk;
	}

	public boolean doesOnlyLeaderExist() {
		return this.studyMembers.size() == 1 && this.studyMembers.get(0).isLeader(this.leader);
	}

	public void changeOpenChatUrl(String openChatUrl) {
		validateEmptyOrWhiteSpace(openChatUrl, STUDY_ONCE_OPEN_CHAT_URL_EMPTY_OR_WHITESPACE);
		this.openChatUrl = openChatUrl;
	}

	private void validateEmptyOrWhiteSpace(String target, ExceptionType exceptionType) {
		if (StringUtils.isEmptyOrWhitespace(target)) {
			throw new CafegoryException(exceptionType);
		}
	}

	public boolean isAttendance(Member member) {
		return studyMembers.stream()
			.anyMatch(s -> s.getId().getMemberId().equals(member.getId()));
	}
}
