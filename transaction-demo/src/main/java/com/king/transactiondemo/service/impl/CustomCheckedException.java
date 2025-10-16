package com.king.transactiondemo.service.impl;

// 自定义一个受检异常，用于实验
public class CustomCheckedException extends Exception {
    public CustomCheckedException(String message) {
        super(message);
    }
}
