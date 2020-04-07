package com.tv.com.tv.event;

import org.springframework.context.ApplicationEvent;

public class EventDemo extends ApplicationEvent {
    private String msg;
    public EventDemo(Object source,String msg) {
        super(source);
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
