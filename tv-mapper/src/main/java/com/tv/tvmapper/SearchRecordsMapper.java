package com.tv.tvmapper;

import java.util.List;

import com.tv.tvpojo.SearchRecords;
import com.tv.util.MyMapper;

public interface SearchRecordsMapper extends MyMapper<SearchRecords> {
	public List<String> getHotwords();
}