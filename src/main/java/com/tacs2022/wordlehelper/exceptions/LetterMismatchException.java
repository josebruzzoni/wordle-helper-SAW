package com.tacs2022.wordlehelper.exceptions;

public class LetterMismatchException  extends RuntimeException {
    public LetterMismatchException(int position, char letter){
        super(String.format("Green letter %c is already in position %d", letter, position+1));
    }
}
