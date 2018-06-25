package com.zxhShop.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zxhShop.pojo.TbBrand;
import com.zxhShop.sellergoods.service.BrandService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/brand")
@RestController//是一个组合注解，@Controller @ResponseBody 对该类所有方法生效
public class BrandController {

    @Reference
    private BrandService brandService;

    @GetMapping("/findAll")
    public List<TbBrand> findAll() {
        return brandService.queryAll();
    }
}
