package com.zxhshop.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zxhshop.pojo.TbSeckillGoods;
import com.zxhshop.seckill.service.SeckillGoodsService;
import com.zxhshop.vo.PageResult;
import com.zxhshop.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/seckillGoods")
@RestController
public class SeckillGoodsController {

    @Reference
    private SeckillGoodsService seckillGoodsService;


    @RequestMapping("/findAll")
    public List<TbSeckillGoods> findAll() {
        return seckillGoodsService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page", defaultValue = "1") Integer page,
                               @RequestParam(value = "rows", defaultValue = "10") Integer rows) {
        return seckillGoodsService.findPage(page, rows);
    }

    @PostMapping("/add")
    public Result add(@RequestBody TbSeckillGoods seckillGoods) {
        try {
            seckillGoodsService.add(seckillGoods);
            return Result.ok("增加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("增加失败");
    }

   /* @GetMapping("/findOne")
    public TbSeckillGoods findOne(Long id) {
        return seckillGoodsService.findOne(id);
    }*/

    @GetMapping("/findOne")
    public TbSeckillGoods findOne(Long id) {
        return seckillGoodsService.findOneFromRedis(id);
    }

    @PostMapping("/update")
    public Result update(@RequestBody TbSeckillGoods seckillGoods) {
        try {
            seckillGoodsService.update(seckillGoods);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    @GetMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            seckillGoodsService.deleteByIds(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     * 分页查询列表
     *
     * @param seckillGoods 查询条件
     * @param page         页号
     * @param rows         每页大小
     * @return
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody TbSeckillGoods seckillGoods, @RequestParam(value = "page", defaultValue = "1") Integer page,
                             @RequestParam(value = "rows", defaultValue = "10") Integer rows) {
        return seckillGoodsService.search(page, rows, seckillGoods);
    }


    /**
     *  查询已经审核库存大于 0 ，开始但是还未结束的秒杀商品列表
     * @return  秒杀商品列表
     */
    @GetMapping("/findList")
    public List<TbSeckillGoods> findList() {
        return seckillGoodsService.findList();
    }


}
