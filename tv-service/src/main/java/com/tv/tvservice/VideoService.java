package com.tv.tvservice;

import com.tv.tvpojo.Videos;
import com.tv.util.PagedResult;

public interface VideoService {
    public String saveVideo(Videos videos);

    public PagedResult queryVideoList(Videos videos, String userId,Integer isSaveRecode,Integer page,Integer pageSize);
}
