package com.example.demo.service;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.example.demo.config.TestConfig;
import com.example.demo.utils.DatabaseCleanup;

@SpringBootTest
@ActiveProfiles("test")
@Import({TestConfig.class})
public class ServiceTest {

	@Autowired
	private DatabaseCleanup databaseCleanup;

	@BeforeEach
	public void setUp() {
		databaseCleanup.execute();
	}
}
