package com.zxhShop.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zxhShop.pojo.TbBrand;
import com.zxhShop.sellergoods.service.BrandService;
import com.zxhShop.vo.PageResult;
import com.zxhShop.vo.Result;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page",defaultValue = "1") Integer page,
                               @RequestParam(value = "rows",defaultValue = "10") Integer rows) {
        return brandService.findPage(page, rows);
    }

    @PostMapping("/add")
    public Result add(@RequestBody TbBrand brand) {

        try {
            brandService.add(brand);
            return Result.ok("新增成功");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Result.fail("新增失败");
    }

    @GetMapping("/findOne")
    public TbBrand findOne(Long id) {
        return brandService.findOne(id);
    }

    @PostMapping("/update")
    public Result update(@RequestBody TbBrand brand) {
        try {
            brandService.update(brand);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Result.fail("修改失败");
    }

    @GetMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            brandService.deleteByIds(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    @PostMapping("/search")
    public PageResult search(@RequestBody TbBrand brand,
                             @RequestParam(value = "page", defaultValue = "1") Integer page,
                             @RequestParam(value = "rows", defaultValue = "10") Integer rows) {
        return brandService.search(brand, page, rows);
    }
}
