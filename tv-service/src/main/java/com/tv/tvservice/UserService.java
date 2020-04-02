package com.tv.tvservice;

import com.tv.tvpojo.Users;

public interface UserService {
    public Users queryUserByUserId(String userId);
    public void updateFaceImage(Users user);
}
