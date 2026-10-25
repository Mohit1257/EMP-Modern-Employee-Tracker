package com.in.emptrack.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiErrorResponse {
    private int status;
    private String error;
    private String message;
    private String path;
    private Map<String, String> fieldErrors;
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
