package com.sergio.taskmanager;
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

    @Test
    public void taskSerializationTest() throws IOException {
        Task task = new Task(1L, "Test Task", "This is a test task", false);
        assertThat(json.write(task)).isStrictlyEqualToJson("expected.json");
        assertThat(json.write(task)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(task)).extractingJsonPathNumberValue("@.id").isEqualTo(1);
        assertThat(json.write(task)).hasJsonPathStringValue("@.title");
        assertThat(json.write(task)).extractingJsonPathStringValue("@.title").isEqualTo("Test Task");
        assertThat(json.write(task)).hasJsonPathStringValue("@.description");
        assertThat(json.write(task)).extractingJsonPathStringValue("@.description").isEqualTo("This is a test task");
        assertThat(json.write(task)).hasJsonPathBooleanValue("@.completed");
        assertThat(json.write(task)).extractingJsonPathBooleanValue("@.completed").isFalse();
    }
}
