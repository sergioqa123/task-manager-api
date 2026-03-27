package com.sergio.taskmanager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

@JsonTest
public class TaskJsonTest {

    @Autowired
    private JacksonTester<Task> json;

    @Autowired
    private JacksonTester<Task[]> jsonList;

    private Task[] tasks;
    
    @BeforeEach
    void setUp() {
        tasks = new Task[] {
            new Task(1L, "Task 1", "Description 1", false),
            new Task(2L, "Task 2", "Description 2", true),
            new Task(3L, "Task 3", "Description 3", false)
        };
    }

    @Test
    public void taskSerializationTest() throws IOException {
        Task task = new Task(1L, "Test Task", "This is a test task", false);
        assertThat(json.write(task)).isStrictlyEqualToJson("single.json");
        assertThat(json.write(task)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(task)).extractingJsonPathNumberValue("@.id").isEqualTo(1);
        assertThat(json.write(task)).hasJsonPathStringValue("@.title");
        assertThat(json.write(task)).extractingJsonPathStringValue("@.title").isEqualTo("Test Task");
        assertThat(json.write(task)).hasJsonPathStringValue("@.description");
        assertThat(json.write(task)).extractingJsonPathStringValue("@.description").isEqualTo("This is a test task");
        assertThat(json.write(task)).hasJsonPathBooleanValue("@.completed");
        assertThat(json.write(task)).extractingJsonPathBooleanValue("@.completed").isFalse();
    }

    @Test
    public void taskDeserializationTest() throws IOException {
        String expected = """
        {
            "id": 1,
            "title": "Test Task",
            "description": "Another test task",
            "completed": false
        }
        """;
        assertThat(json.parse(expected)).isEqualTo(new Task(1L, "Test Task", "Another test task", false));
        assertThat(json.parseObject(expected).id()).isEqualTo(1L);
        assertThat(json.parseObject(expected).title()).isEqualTo("Test Task");
        assertThat(json.parseObject(expected).description()).isEqualTo("Another test task");
        assertThat(json.parseObject(expected).completed()).isFalse();
    }

    @Test
    void taskListSerializationTest() throws IOException {
        assertThat(jsonList.write(tasks)).isStrictlyEqualToJson("list.json");        
    }

    @Test
    void taskListDeserializationTest() throws IOException {
        String expected = """
        [
            {
                "id": 1,
                "title": "Task 1",
                "description": "Description 1",
                "completed": false
            },
            {
                "id": 2,
                "title": "Task 2",
                "description": "Description 2",
                "completed": true
            },
            {
                "id": 3,
                "title": "Task 3",
                "description": "Description 3",
                "completed": false
            }
        ]
        """;
        assertThat(jsonList.parse(expected)).isEqualTo(tasks);
    }
}
