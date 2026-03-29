package com.sergio.taskmanager;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import net.minidev.json.JSONArray;

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

	@Test
	@DirtiesContext
	void shouldCreateANewTask() {
		Task newTask = new Task(null, "New Task", "This is a new task.", false, "sergio");
		ResponseEntity<Void> responseEntity = restTemplate.postForEntity("/tasks", newTask, Void.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		URI locationOfNewTask = responseEntity.getHeaders().getLocation();
		ResponseEntity<String> response = restTemplate.getForEntity(locationOfNewTask, String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		DocumentContext documentContext = JsonPath.parse(response.getBody());
		Number id = documentContext.read("$.id");
		assertThat(id).isNotNull();
		String title = documentContext.read("$.title");
		assertThat(title).isEqualTo("New Task");
		String description = documentContext.read("$.description");
		assertThat(description).isEqualTo("This is a new task.");
		boolean completed = documentContext.read("$.completed");
		assertThat(completed).isFalse();
	}

	@Test
	void shouldReturnAllTasksWhenListIsRequested() {
		ResponseEntity<String> response = restTemplate.getForEntity("/tasks", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		int taskCount = documentContext.read("$.length()");
		assertThat(taskCount).isEqualTo(3);

		JSONArray ids = documentContext.read("$..id");
		assertThat(ids).containsExactlyInAnyOrder(99, 100, 101);
		JSONArray titles = documentContext.read("$..title");
		assertThat(titles).containsExactlyInAnyOrder("Test Task", "Task 100", "Task 101");
		JSONArray descriptions = documentContext.read("$..description");
		assertThat(descriptions).containsExactlyInAnyOrder("This is a test task.", "Description for Task 100", "Description for Task 101");
		JSONArray completions = documentContext.read("$..completed");
		assertThat(completions).containsExactlyInAnyOrder(false, true, false);
	}

	@Test
	void shouldReturnAPageOfTasks() {
		ResponseEntity<String> response = restTemplate.getForEntity("/tasks?page=0&size=1", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		JSONArray page = documentContext.read("$[*]");
		assertThat(page.size()).isEqualTo(1);
	}

	@Test
	void shouldReturnASortedPageOfTasks() {
		ResponseEntity<String> response = restTemplate.getForEntity("/tasks?page=0&size=1&sort=id,desc", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		JSONArray page = documentContext.read("$[*]");
		assertThat(page.size()).isEqualTo(1);
		JSONArray ids = documentContext.read("$..id");
		assertThat(ids).containsExactly(101);

		String title = documentContext.read("$[0].title");
		assertThat(title).isEqualTo("Task 101");
		String description = documentContext.read("$[0].description");
		assertThat(description).isEqualTo("Description for Task 101");
		boolean completed = documentContext.read("$[0].completed");
		assertThat(completed).isFalse();
	}

	@Test
	void shouldReturnASortedPageOfTasksWithNoParametersAndUseDefaultValues() {
		ResponseEntity<String> response = restTemplate.getForEntity("/tasks", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		JSONArray page = documentContext.read("$[*]");
		assertThat(page.size()).isEqualTo(3);
		JSONArray ids = documentContext.read("$..id");
		assertThat(ids).containsExactly(99, 100, 101);
	}
}
