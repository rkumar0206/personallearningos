package com.rksdev.personallearningos.learning.util;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

public class CursorUtils {

    public record Cursor(Instant updatedAt, Long id) {}

    public static String encode(Instant updatedAt, Long id) {
        if (updatedAt == null || id == null) return null;
        String raw = updatedAt.toString() + "_" + id;
        return Base64.getUrlEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    public static Cursor decode(String cursorToken) {
        if (cursorToken == null || cursorToken.isBlank()) return null;
        try {
            String decoded = new String(Base64.getUrlDecoder().decode(cursorToken), StandardCharsets.UTF_8);
            String[] parts = decoded.split("_");
            return new Cursor(Instant.parse(parts[0]), Long.parseLong(parts[1]));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid cursor token format", e);
        }
    }
}