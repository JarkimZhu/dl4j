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
public abstract class LockValue<E> {

    private E value;

    public E getValue() {
        return value;
    }

    public void setValue(E value) {
        this.value = value;
    }

    public abstract void setValue(String token, long timestamp);

    public abstract String getToken();

    public abstract long getTimestamp();
}
