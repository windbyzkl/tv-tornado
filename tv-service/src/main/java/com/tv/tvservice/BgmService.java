package com.tv.tvservice;

import com.tv.tvpojo.Bgm;
import com.tv.util.PagedResult;

import java.util.List;

public interface BgmService {
    List<Bgm> queryBgmList();

    Bgm queryBgmById(String bgmId);

    PagedResult queryBgmList(Integer page, Integer pageSize);

    Bgm saveBgm(String dbPath, String author, String name);

    Bgm delBgm(String bgmId);
}
