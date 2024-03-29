package com.reddish.adoniswebsocket.Utils.Exception;

public class DialogueInfoException extends Exception {
    public ExceptionCode code;

    public DialogueInfoException(ExceptionCode code) {
        this.code = code;
    }
    @Override
    public String getMessage() {
        return code.getDesc();
    }
}
