package com.reddish.adonis.exception;

public class MessageException extends Exception{
    public ExceptionCode code;
    public MessageException(ExceptionCode code) {
        this.code = code;
    }
}
