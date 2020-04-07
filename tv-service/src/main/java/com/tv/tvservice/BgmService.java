package com.tv.tvservice;

import com.tv.tvpojo.Bgm;

import java.util.List;

public interface BgmService {
    public List<Bgm> queryBgmList();
    public Bgm queryBgmById(String bgmId);
}
