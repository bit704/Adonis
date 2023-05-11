package com.reddish.adonis.Manager.Exception;

public class UserInfoException extends Exception {
    public ExceptionCode code;
    public UserInfoException(ExceptionCode code) {
        this.code = code;
    }
}
