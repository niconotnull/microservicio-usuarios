package com.springboot.microservicios.usuarios.exception;

public class HttpException extends RuntimeException{

    public HttpException(String message){
        super(message);
    }

}
