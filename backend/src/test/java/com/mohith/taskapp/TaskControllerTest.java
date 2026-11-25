package com.mohith.taskapp;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class TaskControllerTest {
    @Test
    void rejectsBlankTitle() {
        TaskController controller = new TaskController(null);
        assertThrows(ResponseStatusException.class,
                () -> controller.add(new TaskController.NewTask(" ")));
    }
}
