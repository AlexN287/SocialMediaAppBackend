package com.Licenta.SocialMediaApp.Exceptions;

public class ConversationAlreadyExistsException extends RuntimeException {
    public ConversationAlreadyExistsException(String message) {
        super(message);
    }
}
