package com.rksdev.personallearningos.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Roles {

    ADMIN("ROLE_ADMIN"),
    LEARNER("ROLE_LEARNER");

    private final String value;
}
