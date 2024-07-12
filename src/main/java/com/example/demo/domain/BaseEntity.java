package com.example.demo.domain;

import static com.example.demo.util.TruncatedTimeUtil.*;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public class BaseEntity {

	@Column(updatable = false)
	private LocalDateTime createdDate;
	private LocalDateTime lastModifiedDate;

	@PrePersist
	public void prePersist() {
		LocalDateTime now = LOCAL_DATE_TIME_NOW;
		createdDate = now;
		lastModifiedDate = now;
	}

	@PreUpdate
	public void preUpdate() {
		lastModifiedDate = LOCAL_DATE_TIME_NOW;
	}
}
