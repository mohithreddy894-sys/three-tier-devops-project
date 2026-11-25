package com.mohith.releaseboard;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class ReleaseControllerTest {
    private final ReleaseController controller = new ReleaseController(null, "secret");

    @Test
    void rejectsWrongToken() {
        assertThrows(ResponseStatusException.class, () -> controller.add("wrong",
                new ReleaseController.NewRelease("v1", "abc123", "SUCCESS")));
    }

    @Test
    void rejectsUnknownStatus() {
        assertThrows(ResponseStatusException.class, () -> controller.add("secret",
                new ReleaseController.NewRelease("v1", "abc123", "DONE")));
    }
}
