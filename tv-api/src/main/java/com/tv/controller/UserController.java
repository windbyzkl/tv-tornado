package com.tv.controller;

import com.tv.tvmapper.UsersMapper;
import com.tv.tvpojo.Users;
import com.tv.tvpojo.vo.PublisherVideo;
import com.tv.tvpojo.vo.UsersVO;
import com.tv.tvservice.UserService;
import com.tv.tvservice.VideoService;
import com.tv.util.JsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

@RestController
@RequestMapping("/user")
@Api(value = "用户信息相关接口", tags = {"用户信息相关接口"})
public class UserController extends BaseController {
    @Autowired
    private UserService userService;
    @Autowired
    private VideoService videoService;

    @PostMapping("/queryPblisherInfo")
    @ApiOperation(value = "查询视频发布者信息", notes = "查询视频发布者信息接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "videoId", value = "视频id", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "publishUserId", value = "发布者用户Id", dataType = "String", paramType = "query", required = true),
    })
    public JsonResult queryPublisherInfo(String loginUserId, String videoId, String publisherId) {
        Users publisher = userService.queryUserByUserId(publisherId);
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(publisher, usersVO);
        boolean flag = videoService.isLikeVideo(videoId, loginUserId);
        PublisherVideo publisherVideo = new PublisherVideo();
        publisherVideo.setPublisher(usersVO);
        publisherVideo.setUserLikeVideo(flag);
        return JsonResult.ok(publisherVideo);
    }

    @ApiOperation(value = "查询用户接口", notes = "查询用户接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true,
                    dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "fanId", value = "粉丝id", required = true,
                    dataType = "String", paramType = "query")
    })
    @PostMapping("/queryUser")
    public JsonResult queryUser(String userId, String fanId) throws Exception {
        if (StringUtils.isBlank(userId))
            return JsonResult.errorMsg("用户Id为空.");
        Users user = userService.queryUserByUserId(userId);
        UsersVO usersVO = new UsersVO();
        Jedis jedis = redisTool.getJedisResource();
        BeanUtils.copyProperties(user, usersVO);
        usersVO.setUserToken(jedis.get(super.USER_REDIS_KEY + ":" + usersVO.getId()));
        redisTool.realse(jedis);
        usersVO.setFollow(userService.isFollow(userId,fanId));
        return JsonResult.ok(usersVO);
    }

    @PostMapping("/follow")
    @ApiOperation(value = "用户关注接口",notes = "用户关注接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true,
                    dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "fanId", value = "粉丝id", required = true,
                    dataType = "String", paramType = "query")
    })
    public JsonResult follow(String userId,String fanId){
        userService.follow(userId,fanId);
        return JsonResult.ok();
    }

    @PostMapping("/cancelFollow")
    @ApiOperation(value = "用户取消关注接口",notes = "用户取消关注接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true,
                    dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "fanId", value = "粉丝id", required = true,
                    dataType = "String", paramType = "query")
    })
    public JsonResult cancelFollow(String userId,String fanId){
        userService.cancelFollow(userId,fanId);
        return JsonResult.ok();
    }
}
