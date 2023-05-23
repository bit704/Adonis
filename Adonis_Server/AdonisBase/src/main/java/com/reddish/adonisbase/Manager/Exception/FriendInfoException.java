package com.reddish.adonisbase.Manager.Exception;

public class FriendInfoException extends Exception {
    public ExceptionCode code;

    public FriendInfoException(ExceptionCode code) {
        this.code = code;
    }
}
