package com.reddish.adonisbase.Manager.Exception;

public class DialogueInfoException extends Exception {
    public ExceptionCode code;

    public DialogueInfoException(ExceptionCode code) {
        this.code = code;
    }
}
