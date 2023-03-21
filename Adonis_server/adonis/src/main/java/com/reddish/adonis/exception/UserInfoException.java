package com.reddish.adonis.exception;

public class UserInfoException extends Exception {
    public ExceptionCode code;
    public UserInfoException(ExceptionCode code) {
        this.code = code;
    }
}
