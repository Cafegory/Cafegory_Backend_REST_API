package com.example.demo.repository;

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
import com.example.demo.domain.ReviewImpl;
import com.example.demo.domain.cafe.CafeImpl;
import com.example.demo.domain.member.MemberImpl;
import com.example.demo.domain.member.ThumbnailImage;
import com.example.demo.helper.CafePersistHelper;
import com.example.demo.helper.MemberPersistHelper;
import com.example.demo.helper.ReviewPersistHelper;
import com.example.demo.helper.ThumbnailImagePersistHelper;
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
	private CafePersistHelper cafePersistHelper;
	@Autowired
	private ThumbnailImagePersistHelper thumbnailImagePersistHelper;
	@Autowired
	private MemberPersistHelper memberPersistHelper;
	@Autowired
	private ReviewPersistHelper reviewPersistHelper;

	@Test
	void findAllByCafeId() {
		//given
		CafeImpl cafe = cafePersistHelper.persistDefaultCafe();
		ThumbnailImage thumb = thumbnailImagePersistHelper.persistDefaultThumbnailImage();
		MemberImpl member = memberPersistHelper.persistDefaultMember(thumb);
		reviewPersistHelper.persistDefaultReview(cafe, member);
		reviewPersistHelper.persistDefaultReview(cafe, member);
		em.flush();
		em.clear();
		//when
		List<ReviewImpl> reviews = reviewRepository.findAllByCafeId(cafe.getId());
		//then
		assertThat(reviews.size()).isEqualTo(2);
	}

	@Test
	@DisplayName("페이징 기본값")
	void findAllWithPagingByCafeId() {
		//given
		CafeImpl cafe = cafePersistHelper.persistDefaultCafe();
		ThumbnailImage thumb = thumbnailImagePersistHelper.persistDefaultThumbnailImage();
		MemberImpl member = memberPersistHelper.persistDefaultMember(thumb);

		for (int i = 0; i < 20; i++) {
			reviewPersistHelper.persistDefaultReview(cafe, member);
			reviewPersistHelper.persistDefaultReview(cafe, member);
		}
		em.flush();
		em.clear();
		//when
		Page<ReviewImpl> pagedReviews = reviewRepository.findAllWithPagingByCafeId(cafe.getId(),
			PageRequestCustom.createByDefault());
		//then
		assertThat(pagedReviews.getContent().size()).isEqualTo(10);
		assertThat(pagedReviews.getTotalPages()).isEqualTo(4);
		assertThat(pagedReviews.getSize()).isEqualTo(10);
		assertThat(pagedReviews.getTotalElements()).isEqualTo(40);
	}

}
