package com.reddish.adonis.exception;

public class FriendInfoException extends Exception {
    public ExceptionCode code;

    public FriendInfoException(ExceptionCode code) {
        this.code = code;
    }
}
