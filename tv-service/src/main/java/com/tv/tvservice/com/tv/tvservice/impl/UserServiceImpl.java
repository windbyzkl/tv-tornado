package com.tv.tvservice.com.tv.tvservice.impl;

import com.tv.tvmapper.UsersFansMapper;
import com.tv.tvmapper.UsersMapper;
import com.tv.tvpojo.Users;
import com.tv.tvpojo.UsersFans;
import com.tv.tvservice.UserService;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private UsersFansMapper usersFansMapper;
    @Autowired
    private Sid sid;
    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Users queryUserByUserId(String userId) {
        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",userId);
        Users user = usersMapper.selectOneByExample(example);
        return user;
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateFaceImage(Users user){
        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",user.getId());
        usersMapper.updateByExampleSelective(user,example);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean isFollow(String userId,String fanId){
        Example example = new Example(UsersFans.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("fanId",fanId);
        return usersFansMapper.selectOneByExample(example)==null?false:true;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void follow(String userId,String fanId){
        UsersFans usersFans = new UsersFans();
        String id = sid.nextShort();
        usersFans.setId(id);
        usersFans.setFanId(fanId);
        usersFans.setUserId(userId);
        usersFansMapper.insert(usersFans);
        usersMapper.addFansCount(userId);
        usersMapper.addFollersCount(fanId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void cancelFollow(String userId,String fanId){
        Example example = new Example(UsersFans.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("fanId",fanId);
        usersFansMapper.deleteByExample(example);
        usersMapper.reduceFansCount(userId);
        usersMapper.reduceFollersCount(fanId);
    }
}
