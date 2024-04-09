package com.example.demo.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.domain.member.MemberImpl;
import com.example.demo.domain.review.ReviewImpl;
import com.example.demo.dto.WriterResponse;
import com.example.demo.dto.review.ReviewResponse;
import com.example.demo.dto.review.ReviewSearchResponse;

public class ReviewMapper {

	public List<ReviewSearchResponse> toReviewSearchResponses(List<ReviewImpl> reviews) {
		return reviews.stream()
			.map(review ->
				new ReviewSearchResponse(
					review.getId(),
					produceWriterResponse(
						review.getMember().getId(), review.getMember().getName(),
						review.getMember().getThumbnailImage().getThumbnailImage()
					),
					review.getRate(),
					review.getContent()
				))
			.collect(Collectors.toList());
	}

	private WriterResponse produceWriterResponse(Long memberId, String name, String thumbnailImg) {
		return new WriterResponse(memberId, name, thumbnailImg);
	}

	public ReviewResponse toReviewResponse(ReviewImpl findReview) {
		return ReviewResponse.builder()
			.reviewId(findReview.getId())
			.writer(
				produceWriterResponse(
					findReview.getMember().getId(),
					findReview.getMember().getName(),
					findReview.getMember().getThumbnailImage().getThumbnailImage()
				))
			.rate(findReview.getRate())
			.content(findReview.getContent())
			.build();
	}

	public List<ReviewResponse> toReviewResponses(List<ReviewImpl> reviews) {
		return reviews.stream()
			.map(review ->
				produceReviewResponse(
					review.getId(),
					productWriterResponse(review.getMember()),
					review.getRate(),
					review.getContent()
				)
			)
			.collect(Collectors.toList());
	}

	private ReviewResponse produceReviewResponse(Long reviewId, WriterResponse writerResponse, double rate,
		String content) {
		return ReviewResponse.builder()
			.reviewId(reviewId)
			.writer(writerResponse)
			.rate(rate)
			.content(content)
			.build();
	}

	private WriterResponse productWriterResponse(MemberImpl member) {
		return new WriterResponse(member.getId(),
			member.getName(),
			member.getThumbnailImage().getThumbnailImage()
		);
	}

}
