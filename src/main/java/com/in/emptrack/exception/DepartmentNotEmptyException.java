package com.in.emptrack.exception;

public class DepartmentNotEmptyException extends RuntimeException {
    public DepartmentNotEmptyException(String message) {
        super(message);
    }
}
