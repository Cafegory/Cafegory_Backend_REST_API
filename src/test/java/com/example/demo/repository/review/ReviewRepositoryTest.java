package com.example.demo.repository.review;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.config.QueryDslConfig;
import com.example.demo.config.TestConfig;
import com.example.demo.domain.cafe.Cafe;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.ThumbnailImage;
import com.example.demo.domain.review.Review;
import com.example.demo.helper.save.CafeSaveHelper;
import com.example.demo.helper.save.MemberSaveHelper;
import com.example.demo.helper.save.ReviewSaveHelper;
import com.example.demo.helper.save.ThumbnailImageSaveHelper;
import com.example.demo.util.PageRequestCustom;

@DataJpaTest
@Import({QueryDslConfig.class, TestConfig.class})
@Transactional
class ReviewRepositoryTest {

	@Autowired
	private EntityManager em;
	@Autowired
	private ReviewRepository reviewRepository;
	@Autowired
	private CafeSaveHelper cafePersistHelper;
	@Autowired
	private ThumbnailImageSaveHelper thumbnailImagePersistHelper;
	@Autowired
	private MemberSaveHelper memberPersistHelper;
	@Autowired
	private ReviewSaveHelper reviewPersistHelper;

	@Test
	void findAllByCafeId() {
		//given
		Cafe cafe = cafePersistHelper.persistDefaultCafe();
		ThumbnailImage thumb = thumbnailImagePersistHelper.persistDefaultThumbnailImage();
		Member member = memberPersistHelper.persistDefaultMember(thumb);
		reviewPersistHelper.persistDefaultReview(cafe, member);
		reviewPersistHelper.persistDefaultReview(cafe, member);
		em.flush();
		em.clear();
		//when
		List<Review> reviews = reviewRepository.findAllByCafeId(cafe.getId());
		//then
		assertThat(reviews.size()).isEqualTo(2);
	}

	@Test
	@DisplayName("페이징 기본값")
	void findAllWithPagingByCafeId() {
		//given
		Cafe cafe = cafePersistHelper.persistDefaultCafe();
		ThumbnailImage thumb = thumbnailImagePersistHelper.persistDefaultThumbnailImage();
		Member member = memberPersistHelper.persistDefaultMember(thumb);

		for (int i = 0; i < 20; i++) {
			reviewPersistHelper.persistDefaultReview(cafe, member);
			reviewPersistHelper.persistDefaultReview(cafe, member);
		}
		em.flush();
		em.clear();
		//when
		Page<Review> pagedReviews = reviewRepository.findAllWithPagingByCafeId(cafe.getId(),
			PageRequestCustom.createByDefault());
		//then
		assertThat(pagedReviews.getContent().size()).isEqualTo(10);
		assertThat(pagedReviews.getTotalPages()).isEqualTo(4);
		assertThat(pagedReviews.getSize()).isEqualTo(10);
		assertThat(pagedReviews.getTotalElements()).isEqualTo(40);
	}

}
