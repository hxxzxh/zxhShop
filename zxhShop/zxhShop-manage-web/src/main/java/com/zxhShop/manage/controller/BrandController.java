package com.zxhShop.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zxhShop.pojo.TbBrand;
import com.zxhShop.sellergoods.service.BrandService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/brand")
@RestController//是一个组合注解，@Controller @ResponseBody 对该类所有方法生效
public class BrandController {

    @Reference
    private BrandService brandService;

    @GetMapping("/testBasePage")
    public List<TbBrand> testBasePage(@RequestParam(value = "page",defaultValue = "1") Integer page,
                                  @RequestParam(value = "rows",defaultValue = "10") Integer rows) {
        return (List<TbBrand>) brandService.findPage(page, rows).getRows();
    }


    @GetMapping("/testPage")
    public List<TbBrand> testPage(@RequestParam(value = "page",defaultValue = "1") Integer page,
                                  @RequestParam(value = "rows",defaultValue = "10") Integer rows) {
        return brandService.testPage(page, rows);
    }

    @GetMapping("/findAll")//相当于@RequestMapping指定method为get
    public List<TbBrand> findAll() {
//        return brandService.queryAll();
        return brandService.findAll();
    }
}
