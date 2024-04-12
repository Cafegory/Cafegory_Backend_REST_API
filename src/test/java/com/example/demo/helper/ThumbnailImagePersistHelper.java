package com.example.demo.helper;

import com.example.demo.builder.TestThumbnailImageBuilder;
import com.example.demo.domain.member.ThumbnailImage;
import com.example.demo.helper.entitymanager.EntityManagerForPersistHelper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ThumbnailImagePersistHelper {

	private final EntityManagerForPersistHelper<ThumbnailImage> em;

	public ThumbnailImage persistDefaultThumbnailImage() {
		ThumbnailImage thumbnailImage = new TestThumbnailImageBuilder().build();
		return em.save(thumbnailImage);
	}
}
