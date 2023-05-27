package com.reddish.adoniswebsocket.Utils.Exception;

public class FriendInfoException extends Exception {
    public ExceptionCode code;

    public FriendInfoException(ExceptionCode code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return code.getDesc();
    }
}
