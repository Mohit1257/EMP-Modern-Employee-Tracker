package com.in.emptrack.enums;

import lombok.Getter;

@Getter
public enum EmploymentType {
    FULL_TIME("Full-time"),
    PART_TIME("Part-time"),
    CONTRACT("Contract"),
    INTERN("Intern");

    private final String displayName;

    EmploymentType(String displayName) {
        this.displayName = displayName;
    }

    public static EmploymentType fromString(String text) {
        if (text == null || text.isBlank()) {
            return FULL_TIME;
        }
        for (EmploymentType type : EmploymentType.values()) {
            if (type.name().equalsIgnoreCase(text) || type.displayName.equalsIgnoreCase(text)) {
                return type;
            }
        }
        return FULL_TIME;
    }
}
