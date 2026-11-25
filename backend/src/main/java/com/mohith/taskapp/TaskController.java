package com.mohith.taskapp;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final JdbcTemplate jdbc;

    public TaskController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public record NewTask(String title) {}

    @GetMapping
    public List<Map<String, Object>> list() {
        return jdbc.queryForList("SELECT id, title FROM tasks ORDER BY id");
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void add(@RequestBody NewTask task) {
        if (task.title() == null || task.title().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title is required");
        }
        jdbc.update("INSERT INTO tasks (title) VALUES (?)", task.title());
    }
}
