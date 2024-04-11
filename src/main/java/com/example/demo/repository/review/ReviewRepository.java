package com.example.demo.repository.review;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.domain.review.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	@Query("select r from Review r join fetch r.member where r.cafe.id = :cafeId")
	List<Review> findAllByCafeId(@Param("cafeId") Long cafeId);

	@Query(value = "select r from Review r join fetch r.member where r.cafe.id = :cafeId",
		countQuery = "select count(*) from Review r where r.cafe.id = :cafeId")
	Page<Review> findAllWithPagingByCafeId(@Param("cafeId") Long cafeId, Pageable pageable);
}
