package com.tv.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tv.config.ZKCuratorClient;
import com.tv.enums.BGMOperatorTypeEnum;
import com.tv.tvpojo.Bgm;
import com.tv.tvservice.BgmService;
import com.tv.util.JsonResult;
import com.tv.util.PagedResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/bgm")
@Api(value = "用于bgm请求接口", tags = {"用于访问bgm接口"})
public class BgmController extends BaseController {
    @Autowired
    private BgmService bgmService;
    @Autowired
    private ZKCuratorClient zkCuratorClient;
    @PostMapping("/list")
    @ApiOperation(value = "查询bgm接口", notes = "查询bgm接口")
    public JsonResult queryBgmList() {
        return JsonResult.ok(bgmService.queryBgmList());
    }

    @PostMapping("/bgmList")
    public PagedResult queryBgmList(Integer page) {
        return bgmService.queryBgmList(page, super.PAGE_SIZE);
    }

    @PostMapping("/bgmUpload")
    public JsonResult bgmUpload(MultipartFile file) throws IOException {
        if (file.isEmpty()) return JsonResult.errorMsg("上传文件为空.");
        String fileName = file.getOriginalFilename();
        String dbPath = "/bgm/" + fileName;
//        String finalPath = super.BGM_PATH + dbPath;
        String finalPath = super.BGM_TEMP_PATH + dbPath;
        InputStream is = null;
        FileOutputStream os = null;
        File folder = new File(super.BGM_TEMP_PATH+"bgm");
        if (!folder.exists()) folder.mkdirs();
        try {
            os = new FileOutputStream(new File(finalPath));
            is = file.getInputStream();
            IOUtils.copy(is, os);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                os.flush();
                os.close();
            }
            if (is != null) is.close();
        }
        return JsonResult.ok(dbPath);
    }

    @PostMapping("/addBgm")
    public JsonResult addBgm(String path, String author, String name) throws JsonProcessingException {
        Bgm bgm = bgmService.saveBgm(path, author, name);
        if (bgm != null){
            Map<String,String> nodeInfo = new HashMap<String,String>();
            nodeInfo.put("path",bgm.getPath());
            nodeInfo.put("operType", BGMOperatorTypeEnum.ADD.type);
            ObjectMapper objectMapper = new ObjectMapper();
            String value = objectMapper.writeValueAsString(nodeInfo);
            zkCuratorClient.sendAddBgmNode(bgm.getId(),value);
            return JsonResult.ok();
        }
        else
            return JsonResult.errorMsg("添加bgm异常");

    }

    @PostMapping("/delBgm")
    public JsonResult delBgm(String bgmId) throws JsonProcessingException {
        Bgm bgm  = bgmService.delBgm(bgmId);
        if (bgm != null) {
            Map<String,String> nodeInfo = new HashMap<String,String>();
            nodeInfo.put("path",bgm.getPath());
            nodeInfo.put("operType", BGMOperatorTypeEnum.DELETE.type);
            ObjectMapper objectMapper = new ObjectMapper();
            String value = objectMapper.writeValueAsString(nodeInfo);
            zkCuratorClient.sendDelBgmNode(bgm.getId(),value);
            return JsonResult.ok();
        }
        else return JsonResult.errorMsg("删除bgm失败");
    }
}
