package com.stock.stockmanager.exception;

public class BusinessException extends RuntimeException{
    public  BusinessException(String msg){
        super(msg);
    }
}
