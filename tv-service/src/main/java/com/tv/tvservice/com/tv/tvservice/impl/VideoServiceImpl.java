package com.tv.tvservice.com.tv.tvservice.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tv.tvmapper.SearchRecordsMapper;
import com.tv.tvmapper.VideosMapper;
import com.tv.tvmapper.VideosMapperCustom;
import com.tv.tvpojo.SearchRecords;
import com.tv.tvpojo.Videos;
import com.tv.tvpojo.vo.VideosVO;
import com.tv.tvservice.VideoService;
import com.tv.util.PagedResult;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class VideoServiceImpl implements VideoService {
    @Autowired
    private VideosMapper videosMapper;
    @Autowired
    private VideosMapperCustom videosMapperCustom;
    @Autowired
    private SearchRecordsMapper searchRecordsMapper;
    @Autowired
    private Sid sid;
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public String saveVideo(Videos videos) {
        String id = sid.nextShort();
        videos.setId(id);
        videosMapper.insertSelective(videos);
        return id;
    }

    @Override
    @Transactional(propagation =  Propagation.SUPPORTS)
    public PagedResult queryVideoList(Videos videos,String userId,Integer isSaveRecord ,Integer page,Integer pageSize) {
        String desc = videos.getVideoDesc();
        if (desc != null && isSaveRecord == 1){
            SearchRecords searchRecords = new SearchRecords();
            searchRecords.setContent(desc);
            String id = sid.nextShort();
            searchRecords.setId(id);
            searchRecordsMapper.insertSelective(searchRecords);
        }
        List<VideosVO> list = videosMapperCustom.queryAllVideos(desc,userId);
        PageHelper.startPage(page,pageSize);
        PageInfo<VideosVO> pageInfo = new PageInfo<VideosVO>(list);
        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(page);
        pagedResult.setTotal(pageInfo.getPages());
        pagedResult.setRows(list);
        pagedResult.setRecords(pageInfo.getTotal());
        return pagedResult;
    }
}
