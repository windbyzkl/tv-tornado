package com.tv.service;

import com.tv.tvmapper.UsersMapper;
import com.tv.tvpojo.Users;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginServerImpl implements LoginService{

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private Sid sid;

    public void saveUser(Users user){
        String id = sid.nextShort();
        user.setId(id);
        usersMapper.insertSelective(user);
    }

    public boolean queryUserIsExist(String userName){
        Users user = new Users();
        user.setUsername(userName);
        Users result = usersMapper.selectOne(user);
        return result != null;
    }

    @Override
    public Users queryUser(Users user) {
        return usersMapper.selectOne(user);
    }
}
