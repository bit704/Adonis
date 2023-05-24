package com.reddish.adoniswebsocket.Exception;

public class UserInfoException extends Exception {
    public ExceptionCode code;
    public UserInfoException(ExceptionCode code) {
        this.code = code;
    }
    @Override
    public String getMessage() {
        return code.getDesc();
    }
}
