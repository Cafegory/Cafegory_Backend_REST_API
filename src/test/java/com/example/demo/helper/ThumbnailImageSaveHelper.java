package com.example.demo.helper;

import com.example.demo.domain.member.ThumbnailImage;
import com.example.demo.factory.TestThumbnailImageFactory;
import com.example.demo.repository.thumbnailimage.ThumbnailImageRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ThumbnailImageSaveHelper {

	private final ThumbnailImageRepository thumbnailImageRepository;

	public ThumbnailImage saveThumbnailImage() {
		ThumbnailImage thumbnailImage = TestThumbnailImageFactory.createThumbnailImage();
		return thumbnailImageRepository.save(thumbnailImage);
	}
}
