package com.zxhshop.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zxhshop.pojo.TbGoods;
import com.zxhshop.sellergoods.service.GoodsService;
import com.zxhshop.vo.Goods;
import com.zxhshop.vo.PageResult;
import com.zxhshop.vo.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/goods")
@RestController
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    @RequestMapping("/findAll")
    public List<TbGoods> findAll() {
        return goodsService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page", defaultValue = "1") Integer page,
                               @RequestParam(value = "rows", defaultValue = "10") Integer rows) {
        return goodsService.findPage(page, rows);
    }

    /*@PostMapping("/add")
    public Result add(@RequestBody TbGoods goods) {
        try {
            goodsService.add(goods);
            return Result.ok("增加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("增加失败");
    }*/

    @PostMapping("/add")
    public Result add(@RequestBody Goods goods) {
        try {
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            goods.getGoods().setSellerId(sellerId);
            goods.getGoods().setAuditStatus("0");//未申请审核
            goodsService.addGoods(goods);
            return Result.ok("增加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("增加失败");
    }

    @GetMapping("/findOne")
    public Goods findOne(Long id) {
        return goodsService.findGoodsById(id);
    }

/*
    @GetMapping("/findOne")
    public TbGoods findOne(Long id) {
        return goodsService.findOne(id);
    }
*/

    @PostMapping("/update")
    public Result update(@RequestBody Goods goods) {
        try {
            //检验商家
            TbGoods oldGoods = goodsService.findOne(goods.getGoods().getId());
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            if (!sellerId.equals(oldGoods.getSellerId()) || !sellerId.equals(goods.getGoods().getSellerId())) {
                return Result.fail("操作非法");
            }

            goodsService.updateGoods(goods);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    //@PostMapping("/update")
    public Result update(@RequestBody TbGoods goods) {
        try {
            goodsService.update(goods);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    @GetMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            goodsService.deleteByIds(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     * 分页查询列表
     *
     * @param goods 查询条件
     * @param page  页号
     * @param rows  每页大小
     * @return
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody TbGoods goods, @RequestParam(value = "page", defaultValue = "1") Integer page,
                             @RequestParam(value = "rows", defaultValue = "10") Integer rows) {
        //只能查询当前商家自己的商品
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.setSellerId(sellerId);
        return goodsService.search(page, rows, goods);
    }


    //上传商品图片

    //更新商品状态
    @GetMapping("/updateStatus")
    public Result updateStatus(Long[] ids, String status) {
        try {
            goodsService.updateStatus(ids, status);
            return Result.ok("申请成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("申请失败");
        }
    }

    //更新商品上下架状态
    @GetMapping("/updateMarketable")
    public Result updateMarketable(Long[] ids,String isMarketable) {
        try {
            goodsService.updateMarketable(ids, isMarketable);
            if (isMarketable.equals("0")) {
                return Result.ok("下架成功！");
            }
            return Result.ok("上架成功！");
        } catch (Exception e) {
            e.printStackTrace();
            if (isMarketable.equals("0")) {
                return Result.ok("下架失败！");
            }
            return Result.ok("上架失败！");
        }
    }
}
