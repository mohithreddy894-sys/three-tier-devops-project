package com.mohith.releaseboard;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/releases")
public class ReleaseController {
    private static final Set<String> STATUSES = Set.of("SUCCESS", "FAILED", "ROLLED_BACK");

    private final JdbcTemplate jdbc;
    private final String token;

    public ReleaseController(JdbcTemplate jdbc, @Value("${RELEASE_TOKEN:}") String token) {
        this.jdbc = jdbc;
        this.token = token;
    }

    public record NewRelease(String version, String commitId, String status) {}

    @GetMapping
    public List<Map<String, Object>> list() {
        return jdbc.queryForList("SELECT id, version, commit_id AS commitId, status, "
                + "deployed_at AS deployedAt FROM releases ORDER BY id DESC LIMIT 20");
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void add(@RequestHeader(value = "X-Release-Token", required = false) String given,
                    @RequestBody NewRelease release) {
        if (token.isBlank() || !token.equals(given)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
        if (isEmpty(release.version()) || isEmpty(release.commitId())
                || release.status() == null || !STATUSES.contains(release.status())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid release");
        }
        jdbc.update("INSERT INTO releases (version, commit_id, status) VALUES (?, ?, ?)",
                release.version(), release.commitId(), release.status());
    }

    private static boolean isEmpty(String value) {
        return value == null || value.isBlank();
    }
}
