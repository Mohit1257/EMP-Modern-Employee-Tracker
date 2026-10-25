package com.in.emptrack.enums;

import lombok.Getter;

@Getter
public enum EmploymentStatus {
    ACTIVE("Active", "success"),
    INACTIVE("Inactive", "secondary"),
    ON_LEAVE("On Leave", "warning");

    private final String displayName;
    private final String badgeClass;

    EmploymentStatus(String displayName, String badgeClass) {
        this.displayName = displayName;
        this.badgeClass = badgeClass;
    }

    public static EmploymentStatus fromString(String text) {
        if (text == null || text.isBlank()) {
            return ACTIVE;
        }
        for (EmploymentStatus status : EmploymentStatus.values()) {
            if (status.name().equalsIgnoreCase(text) || status.displayName.equalsIgnoreCase(text)) {
                return status;
            }
        }
        return ACTIVE;
    }
}
