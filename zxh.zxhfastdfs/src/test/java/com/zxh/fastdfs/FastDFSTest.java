package com.zxh.fastdfs;

import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.junit.Test;

import java.io.IOException;

public class FastDFSTest {
    @Test
    public void test() throws Exception {
        //追踪服务器文件的路径
        String conf_filename = ClassLoader.getSystemResource("fastdfs/tracker.conf").getPath();
        //设置全局的配置
        ClientGlobal.init(conf_filename);
        //创建trackerClient
        TrackerClient trackerClient = new TrackerClient();
        //创建trackerServer
        TrackerServer trackerServer = trackerClient.getConnection();
        //创建storageServer：可以为空
        StorageServer storageServer = null;
        //创建存储服务器客户端storageClient
        StorageClient storageClient = new StorageClient((trackerServer), storageServer);


        // 上传文件
        /**
         *  参数 1 ：文件
         *  参数 2 ：文件的后缀
         *  参数 3 ：文件的属性信息
         *  返回结果：形如：
         * group1
         M00/00/00/wKgMqFmfUHiAcpaMAABw0se6LsY441.jpg
         */
        String[] upload_file = storageClient.upload_file("C:\\Users\\黑猩猩哼着歌\\Pictures\\Screenshots\\屏幕截图(1).png", "png", null);
        if (upload_file != null && upload_file.length > 1) {
            for (String str : upload_file) {
                System.out.println(str);
            }

            //获取存储服务器信息
            String groupName = upload_file[0];
            String fileName = upload_file[1];
            ServerInfo[] serverInfos = trackerClient.getFetchStorages(trackerServer, groupName, fileName);
            for (ServerInfo serverInfo : serverInfos) {
                System.out.println("ip=" + serverInfo.getIpAddr() + ":port=" + serverInfo.getPort());
            }

            //组合可以访问的路径
            String url = "http://" + serverInfos[0].getIpAddr() + "/" + groupName + "/" + fileName;
            System.out.println(url);



        }
    }
}
