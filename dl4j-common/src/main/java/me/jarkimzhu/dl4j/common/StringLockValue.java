/*
 * Copyright (c) 2014-2017. JarkimZhu
 * This software can not be used privately without permission
 */

package me.jarkimzhu.dl4j.common;

/**
 * Created on 2017/7/11.
 *
 * @author JarkimZhu
 * @since JDK1.8
 */
public class StringLockValue extends LockValue<String> {

    public StringLockValue() {

    }

    public StringLockValue(String token, long timestamp) {
        setValue(token, timestamp);
    }

    public StringLockValue(String value) {
        setValue(value);
    }

    @Override
    public void setValue(String token, long timestamp) {
        String value = token + "-" + timestamp;
        setValue(value);
    }

    @Override
    public String getToken() {
        String value = getValue();
        if(value != null) {
            return value.split("-")[0];
        }
        return null;
    }

    @Override
    public long getTimestamp() {
        String value = getValue();
        if(value != null) {
            return Long.parseLong(value.split("-")[1]);
        }
        return 0;
    }
}
