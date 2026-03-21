package com.sergio.taskmanager;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskmanagerApplicationTests {

	@Autowired
	TestRestTemplate restTemplate;

	@Test
	void shouldReturnATaskWhenDataIsSaved() {
		ResponseEntity<String> response = restTemplate.getForEntity("/tasks/99", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());

		Number id = documentContext.read("$.id");
		assertThat(id.intValue()).isEqualTo(99);

		String title = documentContext.read("$.title");
		assertThat(title).isEqualTo("Test Task");

		String description = documentContext.read("$.description");
		assertThat(description).isEqualTo("This is a test task.");

		boolean completed = documentContext.read("$.completed");
		assertThat(completed).isFalse();
	}	

	@Test
	void shouldNotReturnATaskWhenIdIsUnknown() {
		ResponseEntity<String> response = restTemplate.getForEntity("/tasks/1000", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody()).isBlank();
	}

}
