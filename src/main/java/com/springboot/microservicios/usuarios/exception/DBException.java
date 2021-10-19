package com.springboot.microservicios.usuarios.exception;

public class DBException extends RuntimeException{

    public DBException(String message) {
        super(message);
    }
}
