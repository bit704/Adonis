package com.reddish.adonis.exception;

public class DialogueInfoException extends Exception {
    public ExceptionCode code;

    public DialogueInfoException(ExceptionCode code) {
        this.code = code;
    }
}
