package com.reddish.adoniswebsocket.Utils.Exception;

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
