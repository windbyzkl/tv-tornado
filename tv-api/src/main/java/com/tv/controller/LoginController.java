package com.tv.controller;

import com.tv.tvpojo.Users;
import com.tv.tvpojo.vo.UsersVO;
import com.tv.tvservice.UserService;
import com.tv.util.JsonResult;
import com.tv.util.MD5Utils;
import com.tv.util.RedisTool;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.tv.service.LoginService;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.io.*;
import java.util.UUID;

import static java.util.UUID.randomUUID;

@RestController
@Api(value = "用户注册和登录接口" ,tags = {"用户登录和注册的Controller"})
public class LoginController extends BaseController{
    @Autowired
    private RedisTool redisTool;
    @Autowired
    private LoginService loginService;
    @Autowired
    private UserService userService;
    private static final String USER_REDIS_KEY = "user-redis-key";
    @ApiOperation(value = "用户注册接口",notes="用户注册的接口")
    @PostMapping("/regist")
    public JsonResult regist(@RequestBody Users user) throws Exception {
        if(StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword()))
            return JsonResult.errorMsg("用户名和密码不能为空.");
        if(loginService.queryUserIsExist(user.getUsername()))
            return JsonResult.errorMsg("用户名已存在.");
        user.setNickname(user.getUsername());
        user.setFansCounts(0);
        user.setFollowCounts(0);
        user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
        loginService.saveUser(user);
        user.setPassword("");
        UsersVO usersVO = setUserToken(user);
        return JsonResult.ok(usersVO);
    }

    private UsersVO setUserToken(Users user){
        Jedis jedis = redisTool.getJedisResource();
        String userToken = UUID.randomUUID().toString();
        UsersVO userVo = new UsersVO();
        BeanUtils.copyProperties(user,userVo);
        userVo.setUserToken(userToken);
        jedis.set(USER_REDIS_KEY+":"+user.getId(),userToken, SetParams.setParams().ex(300));
        redisTool.realse(jedis);
        return userVo;
    }

    @ApiOperation(value = "用户登录接口",notes = "用户登录接口")
    @PostMapping ("/login")
   public JsonResult login(@RequestBody Users user) throws Exception {
         if(StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword()))
            return JsonResult.errorMsg("用户名和密码不能为空.");
         String MD5password = MD5Utils.getMD5Str(user.getPassword());
         user.setPassword(MD5password);
         Users result = loginService.queryUser(user);
         if(result == null)
         return JsonResult.errorMsg("用户名密码错误");
         result.setPassword("");
         UsersVO usersVO = setUserToken(result);
         return JsonResult.ok(usersVO);
    }

    @ApiOperation(value = "查询用户接口",notes = "查询用户接口")
    @ApiImplicitParam(name="userId", value="用户id", required=true,
            dataType="String", paramType="query")
    @PostMapping("/queryUser")
    public JsonResult queryUser(String userId) throws Exception {
        if(StringUtils.isBlank(userId))
            return JsonResult.errorMsg("用户Id为空.");
        Users user = userService.queryUserByUserId(userId);
        UsersVO usersVO = new UsersVO();
        Jedis jedis = redisTool.getJedisResource();
        BeanUtils.copyProperties(user,usersVO);
        usersVO.setUserToken(jedis.get(super.USER_REDIS_KEY+":"+usersVO.getId()));
        redisTool.realse(jedis);
        return JsonResult.ok(usersVO);
    }

    @PostMapping("/loginOut")
    @ApiOperation(value = "用户注销接口",notes = "用户注销接口")
    @ApiImplicitParam(name="userId", value="用户id", required=true,
            dataType="String", paramType="query")
    public JsonResult loginOut(String userId){
        Jedis jedis = redisTool.getJedisResource();
        jedis.del(super.USER_REDIS_KEY+":"+userId);
        redisTool.realse(jedis);
        return JsonResult.ok();
    }

    @PostMapping("/faceUpload")
    @ApiOperation(value="用户上传头像", notes="用户上传头像的接口")
    @ApiImplicitParam(name="userId", value="用户id", required=true,
            dataType="String", paramType="query")
    public JsonResult imgUpload(@RequestParam("file") MultipartFile[] files, String userId) throws IOException {
        InputStream is = null;
        FileOutputStream os = null;
        String finalPath;
        String dbPath = "/"+userId+"/face/";
        File fileFolder = new File(super.TV_FACE_PATH+dbPath);
        if(!fileFolder.exists()){
            fileFolder.mkdirs();
        }
        if(files !=null && files.length>0) {
            try {
                String fileName = files[0].getOriginalFilename();
                dbPath += fileName;
                finalPath = super.TV_FACE_PATH+dbPath;
                is = files[0].getInputStream();
                os = new FileOutputStream(new File(finalPath));
                IOUtils.copy(is, os);
            } catch (IOException e) {
                e.printStackTrace();
                return JsonResult.errorMsg("上传文件失败");
            } finally {
                if (os != null) {
                    os.flush();
                    os.close();
                }
            }
        }else{
            return JsonResult.errorMsg("上传文件失败");
        }
        Users user = new Users();
        user.setId(userId);
        user.setFaceImage(dbPath);
        userService.updateFaceImage(user);
        return JsonResult.ok(dbPath);
    }
}
