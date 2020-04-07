package com.tv.controller;

import com.tv.com.tv.event.EventPublishDemo;
import com.tv.util.RedisTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class BaseController {
    @Autowired
    protected RedisTool redisTool;
    @Value("${com.tv.ffmpegEXE}")
    protected String FFMPEG_PATH;
    @Value("${com.tv.fileSpace}")
    protected String TV_FACE_PATH;
    @Value("${com.tv.videoSpace}")
    protected String TV_VIDEO_PATH;
    @Value("${com.tv.bgmSpace}")
    protected  String BGM_PATH;
    protected final String USER_REDIS_KEY = "user-redis-key";
    protected final Integer PAGE_SIZE = 5;

}
