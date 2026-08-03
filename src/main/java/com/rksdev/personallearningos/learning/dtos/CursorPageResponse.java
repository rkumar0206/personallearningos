package com.rksdev.personallearningos.learning.dtos;

import java.util.List;

public record CursorPageResponse<T>(
        List<T> data,
        String nextCursor,
        boolean hasNext
) {}
