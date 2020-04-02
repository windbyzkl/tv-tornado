package com.tv.controller;

import com.tv.util.RedisTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

@Controller
public class BaseController {
    @Autowired
    protected RedisTool redisTool;
    @Value("${com.tv.fileSpace}")
    protected String TV_FACE_PATH;
    protected final String USER_REDIS_KEY = "user-redis-key";
}
