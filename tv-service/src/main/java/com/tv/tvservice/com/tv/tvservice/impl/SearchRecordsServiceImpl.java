package com.tv.tvservice.com.tv.tvservice.impl;

import com.tv.tvmapper.SearchRecordsMapper;
import com.tv.tvservice.SearchRecordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
public class SearchRecordsServiceImpl implements SearchRecordsService {
    @Autowired
    private SearchRecordsMapper searchRecordsMapper;
    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<String> queryHotWords() {
        return searchRecordsMapper.getHotwords();
    }
}
