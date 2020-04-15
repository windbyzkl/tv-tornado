package com.tv.config;

import java.io.File;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tv.enums.BGMOperatorTypeEnum;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class ZKCuratorClient {

    // zk客户端
    private CuratorFramework client = null;
    final static Logger log = LoggerFactory.getLogger(ZKCuratorClient.class);
    @Autowired
    private ResourceConfig resourceConfig;
    @Value("${com.tv.bgmSpace}")
    private String bgmSpace;

    public void init() {

        if (client != null) {
            return;
        }

        // 重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 5);
        // 创建zk客户端
        System.out.println(resourceConfig.getZookeeperServer() + "------------");
        client = CuratorFrameworkFactory.builder().connectString(resourceConfig.getZookeeperServer())
                .sessionTimeoutMs(10000).retryPolicy(retryPolicy).namespace("admin").build();
        // 启动客户端
        client.start();

        try {
//			String testNodeData = new String(client.getData().forPath("/testnode"));
//			log.info("测试的节点数据为： {}", testNodeData);
            addChildWatch("/bgm");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendAddBgmNode(String bgmId, String nodeInfo) {
        try {
            client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
                    .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                    .forPath("/bgm/" + bgmId, nodeInfo.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendDelBgmNode(String bgmId, String nodeInfo) {
        try {
            client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
                    .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                    .forPath("/bgm/" + bgmId, nodeInfo.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addChildWatch(String nodePath) throws Exception {

        final PathChildrenCache cache = new PathChildrenCache(client, nodePath, true);
        cache.start();
        cache.getListenable().addListener(new PathChildrenCacheListener() {

            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event)
                    throws Exception {
                if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)) {
                    log.info("监听到事件 CHILD_ADDED");
                    String path = event.getData().getPath();
                    String operatorObjStr = new String(event.getData().getData());
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map<String, String> map = new HashMap<>();
                    try {
                        map = objectMapper.readValue(operatorObjStr, Map.class);
                    } catch (Exception e) {
                        log.error("节点值异常解析失败,objStr:" + operatorObjStr);
                        client.delete().forPath(path);
                        return;
                    }
                    String operatorType = map.get("operType");
                    String songPath = map.get("path");
                    // 2. 定义保存到本地的bgm路径
                    String filePath = bgmSpace + songPath;
                    // 3. 定义下载的路径（播放url）
                    String[] arrPath = songPath.split("\\\\");
                    String finalPath = "";
                    // 3.1 处理url的斜杠以及编码
                    for (int i = 0; i < arrPath.length; i++) {
                        if (StringUtils.isNotBlank(arrPath[i])) {
                            finalPath += "/";
                            finalPath += URLEncoder.encode(arrPath[i], "UTF-8");
                        }
                    }
//                    String bgmUrl = resourceConfig.getBgmServer() + finalPath;
                    String bgmUrl = "file:///" + resourceConfig.getFileSpace() + finalPath;
                    System.out.println(bgmUrl + "-------------");
                    if (operatorType.equals(BGMOperatorTypeEnum.ADD.type)) {
                        // 下载bgm到spingboot服务器
                        URL url = new URL(bgmUrl);
                        File file = new File(filePath);
                        FileUtils.copyURLToFile(url, file);
                        client.delete().forPath(path);
                    } else if (operatorType.equals(BGMOperatorTypeEnum.DELETE.type)) {
                        File file = new File(filePath);
                        FileUtils.forceDelete(file);
                        client.delete().forPath(path);
                    }
                }
            }
        });
    }

}
