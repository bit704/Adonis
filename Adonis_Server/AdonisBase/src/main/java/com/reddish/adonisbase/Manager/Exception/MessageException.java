package com.reddish.adonisbase.Manager.Exception;

public class MessageException extends Exception{
    public ExceptionCode code;
    public MessageException(ExceptionCode code) {
        this.code = code;
    }
}
