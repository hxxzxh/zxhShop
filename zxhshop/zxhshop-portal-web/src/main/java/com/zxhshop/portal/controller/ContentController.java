package com.zxhshop.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zxhshop.content.service.ContentService;
import com.zxhshop.pojo.TbContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/content")
@RestController
public class ContentController {

    @Reference
    private ContentService contentService;

    /**
     * 根据内容分类id查询启用的内容列表并降序排序
     * @param categoryId 内容分类id
     * @return 内容列表
     */
    @GetMapping("/findContentListByCategoryId")
    public List<TbContent> findContentListByCategoryId(Long categoryId) {
        System.out.println("----------------------");
        return contentService.findContentListByCategoryId(categoryId);
    }
}
