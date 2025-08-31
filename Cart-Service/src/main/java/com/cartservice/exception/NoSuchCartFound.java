package com.cartservice.exception;

public class NoSuchCartFound extends RuntimeException {
    public NoSuchCartFound(String msg) {
        super(msg);
    }
}
