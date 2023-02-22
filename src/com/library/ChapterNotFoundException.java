package com.library;

import java.lang.Exception;

public class ChapterNotFoundException extends Exception {
    private String code;

    public ChapterNotFoundException(String message) {
        super(message);
    }
}
