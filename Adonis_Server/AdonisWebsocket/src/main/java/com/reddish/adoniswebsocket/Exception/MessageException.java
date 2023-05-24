package com.reddish.adoniswebsocket.Exception;

public class MessageException extends Exception{
    public ExceptionCode code;
    public MessageException(ExceptionCode code) {
        this.code = code;
    }
    @Override
    public String getMessage() {
        return code.getDesc();
    }
}
