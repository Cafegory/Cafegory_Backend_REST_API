package com.example.demo.helper;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.example.demo.builder.TestThumbnailImageBuilder;
import com.example.demo.domain.member.ThumbnailImage;

public class ThumbnailImagePersistHelper {

	@PersistenceContext
	private EntityManager em;

	public ThumbnailImage persistDefaultThumbnailImage() {
		ThumbnailImage thumbnailImage = new TestThumbnailImageBuilder().build();
		em.persist(thumbnailImage);
		return thumbnailImage;
	}
}
