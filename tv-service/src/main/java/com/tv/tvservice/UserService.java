package com.tv.tvservice;

import com.tv.tvpojo.Users;

public interface UserService {
    public Users queryUserByUserId(String userId);
    public void updateFaceImage(Users user);
    public boolean isFollow(String userId,String fanId);
    public void follow(String userId,String fanId);
    public void cancelFollow(String userId,String fanId);
}
