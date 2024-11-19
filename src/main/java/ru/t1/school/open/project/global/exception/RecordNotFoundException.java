package ru.t1.school.open.project.global.exception;

import org.springframework.web.client.HttpClientErrorException;

public class RecordNotFoundException extends RuntimeException {
    public RecordNotFoundException(String message) {
        super(message);
    }
}