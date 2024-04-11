package com.example.demo.service.review;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.demo.domain.cafe.Cafe;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.ThumbnailImage;
import com.example.demo.domain.review.Review;
import com.example.demo.dto.review.ReviewSaveRequest;
import com.example.demo.dto.review.ReviewUpdateRequest;
import com.example.demo.exception.CafegoryException;
import com.example.demo.helper.CafePersistHelper;
import com.example.demo.helper.CafePersistHelperImpl;
import com.example.demo.helper.MemberPersistHelper;
import com.example.demo.helper.MemberPersistHelperImpl;
import com.example.demo.repository.cafe.CafeRepository;
import com.example.demo.repository.cafe.InMemoryCafeRepository;
import com.example.demo.repository.member.InMemoryMemberRepository;
import com.example.demo.repository.member.MemberRepository;
import com.example.demo.repository.review.InMemoryReviewRepository;
import com.example.demo.repository.review.ReviewRepository;

class ReviewServiceTest {
	private final CafeRepository cafeRepository = new InMemoryCafeRepository();
	private final MemberRepository memberRepository = new InMemoryMemberRepository();
	private final ReviewRepository reviewRepository = new InMemoryReviewRepository();
	private final ReviewService reviewService = new ReviewServiceImpl(cafeRepository, memberRepository,
		reviewRepository);

	private final MemberPersistHelper memberPersistHelper = new MemberPersistHelperImpl(memberRepository);

	private final CafePersistHelper cafePersistHelper = new CafePersistHelperImpl(cafeRepository);

	@Test
	@DisplayName("리뷰 저장")
	void saveReview() {
		//given
		Member member1 = memberPersistHelper.persistDefaultMember(new ThumbnailImage(1L, "a"));
		Cafe cafe = cafePersistHelper.persistDefaultCafe();
		//when
		reviewService.saveReview(member1.getId(), cafe.getId(), new ReviewSaveRequest("커피가 맛있어요", 4.5));
		List<Review> findReviews = reviewRepository.findAllByCafeId(cafe.getId());
		//then
		assertThat(findReviews.size()).isEqualTo(1);
	}

	@Test
	@DisplayName("카페 아이디가 존재하지 않으면 예외가 터진다.")
	void saveReview_cafe_exception() {
		//given
		Member member1 = memberPersistHelper.persistDefaultMember(new ThumbnailImage(1L, "a"));
		Cafe cafe = cafePersistHelper.persistDefaultCafe();
		//when
		reviewService.saveReview(member1.getId(), cafe.getId(), new ReviewSaveRequest("커피가 맛있어요", 4.5));
		//then
		assertThatThrownBy(() ->
			reviewService.saveReview(member1.getId(), 10L, new ReviewSaveRequest("커피가 맛있어요", 4.5))
		).isInstanceOf(CafegoryException.class);
	}

	@Test
	@DisplayName("멤버 아이디가 존재하지 않으면 예외가 터진다.")
	void saveReview_member_exception() {
		//given
		Member member1 = memberPersistHelper.persistDefaultMember(new ThumbnailImage(1L, "a"));
		Cafe cafe = cafePersistHelper.persistDefaultCafe();
		//when
		reviewService.saveReview(member1.getId(), cafe.getId(), new ReviewSaveRequest("커피가 맛있어요", 4.5));
		//then
		assertThatThrownBy(() ->
			reviewService.saveReview(10L, cafe.getId(), new ReviewSaveRequest("커피가 맛있어요", 4.5))
		).isInstanceOf(CafegoryException.class);
	}

	@Test
	@DisplayName("리뷰 수정")
	void update_content() {
		//given
		Member member1 = memberPersistHelper.persistDefaultMember(new ThumbnailImage(1L, "a"));
		Cafe cafe = cafePersistHelper.persistDefaultCafe();
		Long savedReviewId = reviewService.saveReview(member1.getId(), cafe.getId(),
			new ReviewSaveRequest("커피가 맛있어요", 4.5));
		//when
		reviewService.updateReview(member1.getId(), savedReviewId, new ReviewUpdateRequest("주차하기 편해요!", 5));
		Review findReview = reviewRepository.findById(savedReviewId).get();
		//then
		assertThat(findReview.getContent()).isEqualTo("주차하기 편해요!");
		assertThat(findReview.getRate()).isEqualTo(5);
	}

	@Test
	@DisplayName("없는 리뷰일경우 예외가 터진다.")
	void update_content_review_exception() {
		//given
		Member member1 = memberPersistHelper.persistDefaultMember(new ThumbnailImage(1L, "a"));
		//then
		assertThatThrownBy(() ->
			reviewService.updateReview(member1.getId(), 100L, new ReviewUpdateRequest("주차하기 편해요!", 5))
		).isInstanceOf(CafegoryException.class);
	}

	@Test
	@DisplayName("자신의 리뷰가 아닐경우 예외가 터진다.")
	void update_content_member_exception() {
		//given
		Member member1 = memberPersistHelper.persistDefaultMember(new ThumbnailImage(1L, "a"));
		Member member2 = memberPersistHelper.persistDefaultMember(new ThumbnailImage(2L, "a"));
		Cafe cafe = cafePersistHelper.persistDefaultCafe();

		Long savedReviewId = reviewService.saveReview(member2.getId(), cafe.getId(),
			new ReviewSaveRequest("커피가 맛있어요", 4.5));
		//then
		assertThatThrownBy(() ->
			reviewService.updateReview(member1.getId(), savedReviewId, new ReviewUpdateRequest("주차하기 편해요!", 5))
		).isInstanceOf(CafegoryException.class);
	}

	@Test
	@DisplayName("리뷰 삭제")
	void delete_review() {
		//given
		Member member1 = memberPersistHelper.persistDefaultMember(new ThumbnailImage(1L, "a"));
		Cafe cafe = cafePersistHelper.persistDefaultCafe();

		Long savedReviewId = reviewService.saveReview(member1.getId(), cafe.getId(),
			new ReviewSaveRequest("커피가 맛있어요", 4.5));
		//when
		reviewService.deleteReview(member1.getId(), savedReviewId);
		Review review = reviewRepository.findById(savedReviewId).orElse(null);
		//then
		assertThat(review).isNull();
	}

}
