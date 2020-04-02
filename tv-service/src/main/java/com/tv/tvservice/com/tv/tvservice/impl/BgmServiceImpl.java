package com.tv.tvservice.com.tv.tvservice.impl;

import com.tv.tvmapper.BgmMapper;
import com.tv.tvpojo.Bgm;
import com.tv.tvservice.BgmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class BgmServiceImpl implements BgmService {
    @Autowired
    private BgmMapper bgmMapper;
    @Override
    public List<Bgm> queryBgmList() {
        return bgmMapper.selectAll();
    }

}
