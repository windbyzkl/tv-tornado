package com.tv.com.tv.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class EventListenerDemo implements ApplicationListener<EventDemo> {

    @Override
    public void onApplicationEvent(EventDemo eventDemo) {
        String msg = eventDemo.getMsg();
        //do someting
        System.out.println("recive msg:"+msg);
    }
}
