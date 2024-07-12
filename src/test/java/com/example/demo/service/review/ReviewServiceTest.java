package com.example.demo.service.review;

import static com.example.demo.exception.ExceptionType.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.domain.cafe.Cafe;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.ThumbnailImage;
import com.example.demo.domain.review.Review;
import com.example.demo.dto.review.ReviewUpdateRequest;
import com.example.demo.exception.CafegoryException;
import com.example.demo.helper.CafeSaveHelper;
import com.example.demo.helper.MemberSaveHelper;
import com.example.demo.helper.ReviewSaveHelper;
import com.example.demo.helper.ThumbnailImageSaveHelper;
import com.example.demo.repository.review.ReviewRepository;
import com.example.demo.service.ServiceTest;

class ReviewServiceTest extends ServiceTest {

	@Autowired
	private ReviewService sut;
	@Autowired
	private CafeSaveHelper cafeSaveHelper;
	@Autowired
	private MemberSaveHelper memberSaveHelper;
	@Autowired
	private ReviewRepository reviewRepository;
	@Autowired
	private ThumbnailImageSaveHelper thumbnailImageSaveHelper;
	@Autowired
	private ReviewSaveHelper reviewSaveHelper;

	@Test
	@DisplayName("자신이 작성한 리뷰만 수정 가능하다.")
	void member_can_update_own_review_only() {
		//given
		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		Member member = memberSaveHelper.saveMember(thumbnailImage);
		Cafe cafe = cafeSaveHelper.saveCafe();
		Review review = reviewSaveHelper.saveReview(cafe, member);
		//when
		sut.updateReview(member.getId(), review.getId(), new ReviewUpdateRequest("리뷰수정", 1));
		//then
		Review updatedReview = reviewRepository.findById(review.getId()).get();
		Assertions.assertAll(
			() -> assertThat(updatedReview.getContent()).isEqualTo("리뷰수정"),
			() -> assertThat(updatedReview.getRate()).isEqualTo(1)
		);
	}

	@Test
	@DisplayName("다른 사람의 리뷰를 수정할 수 없다.")
	void member_can_not_update_another_members_review() {
		//given
		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		Member member1 = memberSaveHelper.saveMember(thumbnailImage);
		Member member2 = memberSaveHelper.saveMember(thumbnailImage);
		Cafe cafe = cafeSaveHelper.saveCafe();
		Review review = reviewSaveHelper.saveReview(cafe, member1);
		//then
		assertThatThrownBy(() -> sut.updateReview(member2.getId(), review.getId(), new ReviewUpdateRequest("리뷰수정", 1)))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(REVIEW_INVALID_MEMBER.getErrorMessage());
	}

	@Test
	@DisplayName("자신이 작성한 리뷰만 삭제 가능하다.")
	void member_can_delete_own_review_only() {
		//given
		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		Member member = memberSaveHelper.saveMember(thumbnailImage);
		Cafe cafe = cafeSaveHelper.saveCafe();
		Review review = reviewSaveHelper.saveReview(cafe, member);
		//when
		sut.deleteReview(member.getId(), review.getId());
		//then
		List<Review> reviews = reviewRepository.findAll();
		assertThat(reviews.isEmpty()).isTrue();
	}

	@Test
	@DisplayName("다른 사람의 리뷰를 삭제할 수 없다.")
	void member_can_not_delete_another_members_review() {
		//given
		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		Member member1 = memberSaveHelper.saveMember(thumbnailImage);
		Member member2 = memberSaveHelper.saveMember(thumbnailImage);
		Cafe cafe = cafeSaveHelper.saveCafe();
		Review review = reviewSaveHelper.saveReview(cafe, member1);
		//then
		assertThatThrownBy(() -> sut.deleteReview(member2.getId(), review.getId()))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(REVIEW_INVALID_MEMBER.getErrorMessage());
	}
}
