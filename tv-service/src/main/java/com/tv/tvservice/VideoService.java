package com.tv.tvservice;

import com.tv.tvpojo.Videos;
import com.tv.util.PagedResult;

public interface VideoService {
    public String saveVideo(Videos videos);

    public PagedResult queryVideoList(Videos videos, String userId, Integer isSaveRecode, Integer page, Integer pageSize);

    public void userLikeVideo(String userId, String videoId, String publishUserId);

    public boolean isLikeVideo(String videoId, String userId);

    public void userUnLikeVideo(String userId, String videoId, String publishUserId);

    public PagedResult queryMyLikeVideos(String userId,Integer page,Integer pageSize);
}
