package com.kiss.core;

public interface ConnectionFactory {

    <T> T getConnection();
}
