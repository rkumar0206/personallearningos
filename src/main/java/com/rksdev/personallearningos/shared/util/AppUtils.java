package com.rksdev.personallearningos.shared.util;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class AppUtils {

    public static String getBaseUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .build()
                .toUriString(); // Returns something like "http://localhost:2266" or "https://api.mydomain.com"
    }

    public static String getSearchStringWithPattern(String search) {
        return (search != null && !search.trim().isEmpty())
                ? "%" + search.trim().toLowerCase() + "%"
                : null;
    }
}
