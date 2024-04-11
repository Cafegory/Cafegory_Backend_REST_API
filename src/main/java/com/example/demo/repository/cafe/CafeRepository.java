package com.example.demo.repository.cafe;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.cafe.Cafe;

public interface CafeRepository extends JpaRepository<Cafe, Long>, CafeRepositoryCustom {
}
