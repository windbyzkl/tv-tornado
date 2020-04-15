package com.tv.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tv.util.JsonResult;
import com.tv.util.RedisTool;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class RequestInterceptor implements HandlerInterceptor {
    @Autowired
    protected RedisTool redisTool;
    protected final String USER_REDIS_KEY = "user-redis-key";
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader("userId");
        String userToken = request.getHeader("userToken");
        if(StringUtils.isBlank(userId) || StringUtils.isBlank(userToken)){
            returnWebResponse(response,"请先登录..");
            return false;
        }
        Jedis jedis = redisTool.getJedisResource();
        String uniqueToken = jedis.get(USER_REDIS_KEY+":"+userId);
        redisTool.realse(jedis);
        if(StringUtils.isBlank(uniqueToken)){
            returnWebResponse(response,"登录过期.");
            return false;
        }else if(!uniqueToken.equals(userToken)){
            returnWebResponse(response,"异地登录.");
            return false;
        }
        return true;
    }

    private void returnWebResponse(HttpServletResponse res,String msg) throws IOException {
        res.setCharacterEncoding("utf-8");
        res.setContentType("text/json");
        OutputStream os = res.getOutputStream();
        JsonResult jr = JsonResult.errorTokenMsg(msg);
        ObjectMapper objectMapper = new ObjectMapper();
        os.write(objectMapper.writeValueAsString(jr).getBytes("utf-8"));
        os.flush();
        os.close();
    }


}
