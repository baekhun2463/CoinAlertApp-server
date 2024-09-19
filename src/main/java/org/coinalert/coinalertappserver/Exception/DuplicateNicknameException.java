package org.coinalert.coinalertappserver.Exception;

public class DuplicateNicknameException extends RuntimeException{
    public DuplicateNicknameException(String message) {
        super(message);
    }
}
