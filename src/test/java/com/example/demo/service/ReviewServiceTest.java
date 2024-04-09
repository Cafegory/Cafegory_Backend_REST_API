package com.example.demo.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.config.TestConfig;
import com.example.demo.domain.ReviewImpl;
import com.example.demo.domain.cafe.CafeImpl;
import com.example.demo.domain.member.MemberImpl;
import com.example.demo.domain.member.ThumbnailImage;
import com.example.demo.dto.ReviewSaveRequest;
import com.example.demo.dto.ReviewUpdateRequest;
import com.example.demo.exception.CafegoryException;
import com.example.demo.helper.CafePersistHelper;
import com.example.demo.helper.MemberPersistHelper;
import com.example.demo.helper.ThumbnailImagePersistHelper;
import com.example.demo.repository.ReviewRepository;

@SpringBootTest
@Transactional
@Import(TestConfig.class)
class ReviewServiceTest {

	@Autowired
	private ReviewService reviewService;
	@Autowired
	private ReviewRepository reviewRepository;
	@Autowired
	private ThumbnailImagePersistHelper thumbnailImagePersistHelper;
	@Autowired
	private MemberPersistHelper memberPersistHelper;
	@Autowired
	private CafePersistHelper cafePersistHelper;

	@Test
	@DisplayName("리뷰 저장")
	void saveReview() {
		//given
		ThumbnailImage thumbnailImage1 = thumbnailImagePersistHelper.persistDefaultThumbnailImage();
		MemberImpl member1 = memberPersistHelper.persistDefaultMember(thumbnailImage1);
		CafeImpl cafe = cafePersistHelper.persistDefaultCafe();
		//when
		reviewService.saveReview(member1.getId(), cafe.getId(), new ReviewSaveRequest("커피가 맛있어요", 4.5));
		List<ReviewImpl> findReviews = reviewRepository.findAllByCafeId(cafe.getId());
		//then
		assertThat(findReviews.size()).isEqualTo(1);
	}

	@Test
	@DisplayName("카페 아이디가 존재하지 않으면 예외가 터진다.")
	void saveReview_cafe_exception() {
		//given
		ThumbnailImage thumbnailImage1 = thumbnailImagePersistHelper.persistDefaultThumbnailImage();
		MemberImpl member1 = memberPersistHelper.persistDefaultMember(thumbnailImage1);
		CafeImpl cafe = cafePersistHelper.persistDefaultCafe();
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
		ThumbnailImage thumbnailImage1 = thumbnailImagePersistHelper.persistDefaultThumbnailImage();
		MemberImpl member1 = memberPersistHelper.persistDefaultMember(thumbnailImage1);
		CafeImpl cafe = cafePersistHelper.persistDefaultCafe();
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
		ThumbnailImage thumbnailImage1 = thumbnailImagePersistHelper.persistDefaultThumbnailImage();
		MemberImpl member1 = memberPersistHelper.persistDefaultMember(thumbnailImage1);
		CafeImpl cafe = cafePersistHelper.persistDefaultCafe();
		Long savedReviewId = reviewService.saveReview(member1.getId(), cafe.getId(),
			new ReviewSaveRequest("커피가 맛있어요", 4.5));
		//when
		reviewService.updateReview(member1.getId(), savedReviewId, new ReviewUpdateRequest("주차하기 편해요!", 5));
		ReviewImpl findReview = reviewRepository.findById(savedReviewId).get();
		//then
		assertThat(findReview.getContent()).isEqualTo("주차하기 편해요!");
		assertThat(findReview.getRate()).isEqualTo(5);
	}

	@Test
	@DisplayName("없는 리뷰일경우 예외가 터진다.")
	void update_content_review_exception() {
		//given
		ThumbnailImage thumbnailImage1 = thumbnailImagePersistHelper.persistDefaultThumbnailImage();
		MemberImpl member1 = memberPersistHelper.persistDefaultMember(thumbnailImage1);
		//then
		assertThatThrownBy(() ->
			reviewService.updateReview(member1.getId(), 100L, new ReviewUpdateRequest("주차하기 편해요!", 5))
		).isInstanceOf(CafegoryException.class);
	}

	@Test
	@DisplayName("자신의 리뷰가 아닐경우 예외가 터진다.")
	void update_content_member_exception() {
		//given
		ThumbnailImage thumbnailImage1 = thumbnailImagePersistHelper.persistDefaultThumbnailImage();
		ThumbnailImage thumbnailImage2 = thumbnailImagePersistHelper.persistDefaultThumbnailImage();
		MemberImpl member1 = memberPersistHelper.persistDefaultMember(thumbnailImage1);
		MemberImpl member2 = memberPersistHelper.persistDefaultMember(thumbnailImage2);
		CafeImpl cafe = cafePersistHelper.persistDefaultCafe();

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
		ThumbnailImage thumbnailImage1 = thumbnailImagePersistHelper.persistDefaultThumbnailImage();
		MemberImpl member1 = memberPersistHelper.persistDefaultMember(thumbnailImage1);
		CafeImpl cafe = cafePersistHelper.persistDefaultCafe();

		Long savedReviewId = reviewService.saveReview(member1.getId(), cafe.getId(),
			new ReviewSaveRequest("커피가 맛있어요", 4.5));
		//when
		reviewService.deleteReview(member1.getId(), savedReviewId);
		ReviewImpl review = reviewRepository.findById(savedReviewId).orElse(null);
		//then
		assertThat(review).isNull();
	}

}
