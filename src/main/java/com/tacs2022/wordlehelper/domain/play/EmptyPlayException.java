package com.tacs2022.wordlehelper.domain.play;

public class EmptyPlayException extends RuntimeException{
    public EmptyPlayException(String message) {
        super(message);
    }
}
