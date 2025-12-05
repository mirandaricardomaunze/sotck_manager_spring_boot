package com.stock.stockmanager.exception;

public class DuplicateResourceException extends RuntimeException{
    public  DuplicateResourceException(String msg){
        super(msg);
    }
}
