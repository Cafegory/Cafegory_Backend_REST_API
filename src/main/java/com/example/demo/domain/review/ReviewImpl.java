package com.example.demo.domain.review;

import static com.example.demo.exception.ExceptionType.*;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.thymeleaf.util.StringUtils;

import com.example.demo.domain.cafe.Cafe;
import com.example.demo.domain.member.MemberImpl;
import com.example.demo.exception.CafegoryException;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "review")
public class ReviewImpl implements Review {

	private static final int MIN_RATE = 0;
	private static final int MAX_RATE = 5;
	private static final int MAX_CONTENT_SIZE = 200;

	@Id
	@GeneratedValue
	@Column(name = "review_id")
	private Long id;

	private String content;
	private double rate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cafe_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Cafe cafe;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private MemberImpl member;

	@Builder
	private ReviewImpl(Long id, String content, double rate, Cafe cafe, MemberImpl member) {
		validateEmptyOrWhitespace(content);
		validateRateRange(rate);
		this.id = id;
		this.content = content;
		this.rate = rate;
		this.cafe = cafe;
		this.member = member;
	}

	private void validateRateRange(double rate) {
		if (!(rate >= MIN_RATE && rate <= MAX_RATE)) {
			throw new CafegoryException(REVIEW_INVALID_RATE_RANGE);
		}
	}

	private void validateEmptyOrWhitespace(String content) {
		if (StringUtils.isEmptyOrWhitespace(content)) {
			throw new CafegoryException(REVIEW_CONTENT_EMPTY_OR_WHITESPACE);
		}
	}

	@Override
	public void updateContent(String content) {
		validateContentSize(content);
		this.content = content;
	}

	private void validateContentSize(String content) {
		if (content.length() > MAX_CONTENT_SIZE) {
			throw new CafegoryException(REVIEW_OVER_CONTENT_SIZE);
		}
	}

	@Override
	public void updateRate(double rate) {
		validateRateRange(rate);
		this.rate = rate;
	}

	public boolean isValidMember(MemberImpl member) {
		return this.member.equals(member);
	}

}
