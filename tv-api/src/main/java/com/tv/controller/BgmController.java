package com.tv.controller;

import com.tv.tvservice.BgmService;
import com.tv.util.JsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bgm")
@Api(value = "用于bgm请求接口",tags = {"用于访问bgm接口"})
public class BgmController extends BaseController {
    @Autowired
    private BgmService bgmService;
    @PostMapping("/list")
    @ApiOperation(value = "查询bgm接口",notes = "查询bgm接口")
    public JsonResult queryBgmList(){
        return JsonResult.ok(bgmService.queryBgmList());
    }
}
