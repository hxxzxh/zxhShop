package com.zxhshop.manage.controller;

import com.zxhshop.common.util.FastDFSClient;
import com.zxhshop.vo.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/upload")
@RestController
public class UploadController {
    @PostMapping
    public Result upload(MultipartFile file) {

        try {
            String file_ext_name = file.getOriginalFilename().
                    substring(file.getOriginalFilename().lastIndexOf(".") + 1);
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:fastdfs/tracker.conf");
            String url = fastDFSClient.uploadFile(file.getBytes(), file_ext_name);
            return Result.ok(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("上传图片失败");
    }
}
