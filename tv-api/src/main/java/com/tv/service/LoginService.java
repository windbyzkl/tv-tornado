package com.tv.service;

import com.tv.tvpojo.Users;

public interface LoginService {
    public void saveUser(Users user);
    public boolean queryUserIsExist(String userName);
    public Users queryUser(Users user);
}
