package com.tv.controller;

import com.tv.enums.VideoStatusEnum;
import com.tv.tvpojo.Bgm;
import com.tv.tvpojo.Videos;
import com.tv.tvservice.BgmService;
import com.tv.tvservice.SearchRecordsService;
import com.tv.tvservice.VideoService;
import com.tv.util.FFMpegUtils;
import com.tv.util.JsonResult;
import io.swagger.annotations.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/video")
@Api(value = "视频接口",tags = {"视频接口"})
public class VideoController extends BaseController{
    @Autowired
    private BgmService bgmService;
    @Autowired
    private VideoService videoService;
    @Autowired
    private SearchRecordsService searchRecordsService;
    @PostMapping(value = "/upload",headers = "content-type=multipart/form-data")
    @ApiOperation(value = "上传视频接口",notes = "上传视频接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",value = "用户id",dataType = "String",paramType = "form",required = true),
            @ApiImplicitParam(name = "videoDesc",value = "视频描述",dataType = "String",paramType = "form",required = false),
            @ApiImplicitParam(name = "videoWidth",value = "视频宽度",dataType = "String",paramType = "form",required = true),
            @ApiImplicitParam(name = "videoHeight",value = "视频高度",dataType = "String",paramType = "form",required = true),
            @ApiImplicitParam(name = "bgmId",value = "bgmId",dataType = "String",paramType = "form",required = false),
            @ApiImplicitParam(name = "videoSeconds",value = "视频时长",dataType = "String",paramType = "form",required = true)
    })
    public JsonResult uploadVideo(@ApiParam(value = "短视频",required = true) MultipartFile file, String userId, String videoDesc, int videoWidth,
                                  int videoHeight, String bgmId, double videoSeconds) throws IOException {
        InputStream is = null;
        FileOutputStream out = null;
        String basePath = super.TV_VIDEO_PATH;
        String dbPath = "/video/"+userId+"/";
        File folder = new File(basePath+dbPath);
        if(!folder.exists()) folder.mkdirs();
        if(file == null ){
            return JsonResult.errorMsg("视频文件不能为空");
        }
        String fileName = file.getOriginalFilename();
        System.out.println("fileName:"+fileName);
        String arrayFilenameItem[] =  fileName.split("\\.");
        String fileNamePrefix = "";
        for (int i = 0 ; i < arrayFilenameItem.length-1 ; i ++) {
            fileNamePrefix += arrayFilenameItem[i];
        }
        fileNamePrefix =fileNamePrefix+".jpg";
        dbPath +=fileName;
        File video = new File(basePath+dbPath);
        try {
            is = file.getInputStream();
            out = new FileOutputStream(video);
            IOUtils.copy(is,out);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(out != null){
                out.flush();
                out.close();
            }
        }
        //update userInfo videoInfo
        FFMpegUtils ffMpegUtils = new FFMpegUtils(super.FFMPEG_PATH);
        if(StringUtils.isNotBlank(bgmId)){
            String videoPath = basePath+dbPath;
            Bgm bgm = bgmService.queryBgmById(bgmId);
            String bgmPath = super.BGM_PATH+bgm.getPath();
            String finalVideoName = UUID.randomUUID().toString()+".mp4";
            dbPath = "/video/"+userId+"/"+finalVideoName;
            String finalVideoPath = basePath+dbPath;
            ffMpegUtils.mergeVideoMp3(videoPath,bgmPath,finalVideoPath,videoSeconds);
            System.out.println("finalVideoPath:"+finalVideoPath);
            System.out.println("bgmPath:"+bgmPath);
        }
        //视频截图操作
        System.out.println("imgpath:"+basePath+fileNamePrefix);
        System.out.println("inuptpath:"+basePath+dbPath);
        ffMpegUtils.getVideoImg(basePath+dbPath,basePath+dbPath+fileNamePrefix);
        Videos videoDemo = new Videos();
        videoDemo.setUserId(userId);
        videoDemo.setVideoDesc(videoDesc);
        videoDemo.setVideoHeight(videoHeight);
        videoDemo.setVideoPath(dbPath);
        videoDemo.setAudioId(bgmId);
        videoDemo.setVideoSeconds((float)videoSeconds);
        videoDemo.setVideoWidth(videoWidth);
        videoDemo.setCoverPath(dbPath+fileNamePrefix);
        videoDemo.setStatus(VideoStatusEnum.SUCCESS.value);
        videoDemo.setCreateTime(new Date());
        String videoId = videoService.saveVideo(videoDemo);
        return JsonResult.ok(videoId);
    }

    @PostMapping("/queryAllVideoList")
    @ApiOperation(value = "视频列表查询接口",notes = "视频列表查询接口")
    public JsonResult queryAllVideoList(@RequestBody Videos videos, String userId, Integer isSaveRecord, Integer page, Integer pageSize){
        if(page == null) page = 1;
        if(pageSize == null) pageSize = super.PAGE_SIZE;
        return JsonResult.ok(videoService.queryVideoList(videos,userId,isSaveRecord,page,pageSize));
    }

    @PostMapping("/hot")
    @ApiOperation(value = "热词查询接口",notes = "热词查询接口")
    public JsonResult queryHotList(){
        return JsonResult.ok(searchRecordsService.queryHotWords());
    }

    @PostMapping("/userLike")
    @ApiOperation(value = "视频点赞接口",notes = "视频点赞接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",value = "用户id",dataType = "String",paramType = "query",required = true),
            @ApiImplicitParam(name = "videoId",value = "视频id",dataType = "String",paramType = "query",required = true),
            @ApiImplicitParam(name = "publishUserId",value = "发布者用户Id",dataType = "String",paramType = "query",required = true),
    })
    public JsonResult userLike(String userId,String videoId,String publishUserId){
        videoService.userLikeVideo(userId,videoId,publishUserId);
        return JsonResult.ok();
    }

    @PostMapping("/userUnLike")
    @ApiOperation(value =  "视频取消点赞接口",notes = "视频取消点赞接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",value = "用户id",dataType = "String",paramType = "query",required = true),
            @ApiImplicitParam(name = "videoId",value = "视频id",dataType = "String",paramType = "query",required = true),
            @ApiImplicitParam(name = "publishUserId",value = "发布者用户Id",dataType = "String",paramType = "query",required = true),
    })
    public JsonResult userUnLike(String userId,String videoId,String publishUserId){
        videoService.userUnLikeVideo(userId,videoId,publishUserId);
        return JsonResult.ok();
    }

    @PostMapping("/queryMyLikeVideos")
    public JsonResult queryMyLikeVideos(String userId,Integer page,Integer pageSize){
        if(StringUtils.isBlank(userId)){
            return JsonResult.errorMsg("参数错误");
        }
        if(page == null){
            page = 1;
        }
        if(pageSize == null){
            pageSize = super.PAGE_SIZE;
        }
        return JsonResult.ok(videoService.queryMyLikeVideos(userId,page,pageSize));
    }
}
