package com.tv.tvservice.com.tv.tvservice.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tv.tvmapper.BgmMapper;
import com.tv.tvpojo.Bgm;
import com.tv.tvservice.BgmService;
import com.tv.util.PagedResult;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
public class BgmServiceImpl implements BgmService {
    @Autowired
    private BgmMapper bgmMapper;
    @Autowired
    private Sid sid;
    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Bgm> queryBgmList() {
        return bgmMapper.selectAll();
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Bgm queryBgmById(String bgmId) {
        return bgmMapper.selectByPrimaryKey(bgmId);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public PagedResult queryBgmList(Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<Bgm> list = bgmMapper.selectAll();
        PageInfo<Bgm> pageList = new PageInfo<>(list);
        PagedResult result = new PagedResult();
        result.setTotal(pageList.getPages());
        result.setRows(list);
        result.setPage(page);
        result.setRecords(pageList.getTotal());
        return result;
    }
    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Bgm saveBgm(String dbPath,String author,String name){
        String id = sid.nextShort();
        Bgm bgm = new Bgm();
        bgm.setAuthor(author);
        bgm.setId(id);
        bgm.setName(name);
        bgm.setPath(dbPath);
        int count = bgmMapper.insert(bgm);
        if(count<=0) return null;
        return bgm;
    }
    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Bgm delBgm(String bgmId){
        Bgm bgm = new Bgm();
        bgm.setId(bgmId);
        bgm = bgmMapper.selectByPrimaryKey(bgm);
        int count = bgmMapper.deleteByPrimaryKey(bgm);
        if(count<=0) return null;
        return bgm;
    }

}
