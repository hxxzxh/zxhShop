package com.zxhshop.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zxhshop.pojo.TbItemCat;
import com.zxhshop.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/itemSearch")
@RestController
public class ItemSearchController {
    @Reference
    private ItemSearchService itemSearchService;

    /**
     * 根据搜索关键字搜索商品列表
     * @param searchMap 搜索条件
     * @return 搜索结果
     */
    @PostMapping("/search")
    public Map<String, Object> search(@RequestBody Map<String, Object> searchMap) {
        return itemSearchService.search(searchMap);
    }

}
